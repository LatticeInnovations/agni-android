package com.latticeonfhir.core.data.repository.server.signup

import com.latticeonfhir.core.model.server.authentication.Otp
import com.latticeonfhir.core.model.server.register.Register
import com.latticeonfhir.core.data.repository.server.authentication.AuthenticationRepository
import com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.model.enums.RegisterTypeEnum
import com.latticeonfhir.core.model.server.authentication.Login
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.network.api.SignUpApiService
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val signUpApiService: SignUpApiService,
    private val preferenceRepository: PreferenceRepository,
    private val authenticationRepository: AuthenticationRepository
) : SignUpRepository {

    override suspend fun verification(
        userContact: String,
        type: RegisterTypeEnum
    ): ResponseMapper<String?> {
        return ApiResponseConverter.convert(
            signUpApiService.verification(
                login = Login(
                    userContact = userContact,
                    type = type
                )
            )
        )
    }

    override suspend fun otpVerification(
        userContact: String,
        otp: Int,
        type: RegisterTypeEnum
    ): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            signUpApiService.verificationOtp(
                otp = Otp(
                    userContact = userContact,
                    otp = otp,
                    type = type
                )
            )
        )
    }

    override suspend fun register(
        register: Register,
        tempAuthToken: String
    ): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            signUpApiService.register(tempAuthToken, register)
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
                authenticationRepository.getUserDetails()
            }
        }
    }
}