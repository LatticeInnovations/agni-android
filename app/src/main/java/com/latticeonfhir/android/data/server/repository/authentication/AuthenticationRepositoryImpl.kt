package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import timber.log.Timber
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

    private suspend fun getUserDetails(): ResponseMapper<UserResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.getUserDetails()
        ).apply {
            if (this is ApiEndResponse) {
                body.apply {
                    preferenceRepository.setUserFhirId(userId)
                    preferenceRepository.setUserName(userName)
                    preferenceRepository.setUserRole(role[0].role)
                    userEmail?.let { email -> preferenceRepository.setUserEmail(email) }
                    mobileNumber?.let { mobileNumber -> preferenceRepository.setUserMobile(mobileNumber) }
                }
            }
        }
    }
}