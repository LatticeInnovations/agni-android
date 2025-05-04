package com.latticeonfhir.core.data.repository.server.authentication

import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.model.server.authentication.Login
import com.latticeonfhir.core.model.server.authentication.Otp
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.model.server.user.UserResponse
import com.latticeonfhir.core.network.api.AuthenticationApiService
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEmptyResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationApiService: AuthenticationApiService,
    private val preferenceRepository: PreferenceRepository
) : AuthenticationRepository {

    override suspend fun login(userContact: String): ResponseMapper<String?> {
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
            authenticationApiService.login(
                Login(
                    userContact = userContact
                )
            )
        )
    }

    override suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse> {
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
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
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
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
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
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