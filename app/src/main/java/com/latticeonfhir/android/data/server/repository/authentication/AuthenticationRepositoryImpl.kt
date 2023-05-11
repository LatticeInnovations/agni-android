package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceRepository: PreferenceRepository
) : AuthenticationRepository {

    override suspend fun login(userContact: String): ResponseMapper<String?> {
        return ApiResponseConverter.convert(
            apiService.login(
                Login(
                    userContact = userContact
                )
            )
        )
    }

    override suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            apiService.validateOtp(
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
            apiService.getUserDetails()
        ).apply {
            if (this is ApiEndResponse) {
                body.apply {
                    preferenceRepository.setUserName(userName)
                    preferenceRepository.setUserRole(role)
                    email?.let { email -> preferenceRepository.setUserEmail(email) }
                    mobileNumber?.let { mobileNumber -> preferenceRepository.setUserMobile(mobileNumber) }
                }
            }
        }
    }
}