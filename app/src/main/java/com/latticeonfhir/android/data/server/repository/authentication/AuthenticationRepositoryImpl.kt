package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationApiService: AuthenticationApiService,
    private val preferenceRepository: PreferenceRepository
) : AuthenticationRepository {

    override suspend fun login(userContact: String): ResponseMapper<String?> {
        return ApiResponseConverter.convert(
            authenticationApiService.login(
                Login(
                    userContact = userContact
                )
            )
        )
    }

    override suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.validateOtp(
                Otp(
                    userContact = userContact,
                    otp = otp
                )
            )
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
                getUserDetails()
            }
        }
    }

    override suspend fun getUserDetails(): ResponseMapper<UserResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.getUserDetails()
        ).apply {
            if (this is ApiEndResponse) {
                body.apply {
                    preferenceRepository.setUserFhirId(userId)
                    preferenceRepository.setUserName(userName)
                    preferenceRepository.setUserRoleId(role[0].roleId)
                    preferenceRepository.setUserRole(role[0].role)
                    preferenceRepository.setOrganizationFhirId(role[0].orgId)
                    preferenceRepository.setOrganization(role[0].orgName)
                    userEmail?.let { email -> preferenceRepository.setUserEmail(email) }
                    mobileNumber?.let { mobileNumber ->
                        preferenceRepository.setUserMobile(
                            mobileNumber
                        )
                    }
                }
            }
        }
    }

    override suspend fun deleteAccount(tempToken: String): ResponseMapper<String?> {
        val deleteUserResponse = authenticationApiService.deleteUserDetails(tempToken)
        return ApiResponseConverter.convert(
            deleteUserResponse
        ).run {
            if (this is ApiEmptyResponse) {
                return ApiEndResponse(body = deleteUserResponse.body()?.message!!)
            } else {
                this
            }
        }
    }
}