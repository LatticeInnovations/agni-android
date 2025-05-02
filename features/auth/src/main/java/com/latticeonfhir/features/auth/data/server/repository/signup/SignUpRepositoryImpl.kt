package com.latticeonfhir.features.auth.data.server.repository.signup

import com.latticeonfhir.android.auth.data.server.model.authentication.Login
import com.latticeonfhir.features.auth.data.server.model.authentication.Otp
import com.latticeonfhir.android.auth.data.server.model.authentication.TokenResponse
import com.latticeonfhir.core.auth.data.server.model.register.Register
import com.latticeonfhir.core.auth.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.features.auth.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.android.utils.converters.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val signUpApiService: com.latticeonfhir.features.auth.data.server.api.SignUpApiService,
    private val preferenceRepository: PreferenceRepository,
    private val authenticationRepository: AuthenticationRepository
) : SignUpRepository {

    override suspend fun verification(
        userContact: String,
        type: RegisterTypeEnum
    ): ResponseMapper<String?> {
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
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
        return com.latticeonfhir.core.utils.converters.responsemapper.ApiResponseConverter.convert(
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
        return com.latticeonfhir.android.utils.converters.responsemapper.ApiResponseConverter.convert(
            signUpApiService.register(tempAuthToken, register)
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
                authenticationRepository.getUserDetails()
            }
        }
    }
}