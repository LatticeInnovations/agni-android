package com.latticeonfhir.android.data.server.repository.signup

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.SignUpApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.Register
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(private val signUpApiService: SignUpApiService,
                                               private val preferenceRepository: PreferenceRepository,
    private val authenticationRepository: AuthenticationRepository): SignUpRepository {

    override suspend fun verification(userContact: String): ResponseMapper<String?> {
        return ApiResponseConverter.convert(
            signUpApiService.verification(
                login = Login(
                    userContact = userContact
                )
            )
        )
    }

    override suspend fun otpVerification(userContact: String, otp: Int): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            signUpApiService.verificationOtp(
                otp = Otp(
                    userContact = userContact,
                    otp = otp
                )
            )
        ).apply {
            if(this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
            }
        }
    }

    override suspend fun register(register: Register): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            signUpApiService.register(register)
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
                authenticationRepository.getUserDetails()
            }
        }
    }
}