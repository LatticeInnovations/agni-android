package com.heartcare.agni.data.server.repository.signup

import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.server.api.SignUpApiService
import com.heartcare.agni.data.server.enums.RegisterTypeEnum
import com.heartcare.agni.data.server.model.authentication.Login
import com.heartcare.agni.data.server.model.authentication.Otp
import com.heartcare.agni.data.server.model.authentication.TokenResponse
import com.heartcare.agni.data.server.model.register.Register
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepository
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
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