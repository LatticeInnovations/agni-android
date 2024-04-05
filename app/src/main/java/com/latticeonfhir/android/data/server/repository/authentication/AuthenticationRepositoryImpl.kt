package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.request.Login
import com.latticeonfhir.android.data.server.model.authentication.request.Otp
import com.latticeonfhir.android.data.server.model.authentication.response.OtpResponse
import com.latticeonfhir.android.data.server.model.authentication.response.LoginResponse
import com.latticeonfhir.android.utils.constants.patient.ISDCodes.ISD_INDIA
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationApiService: AuthenticationApiService,
    private val preferenceRepository: PreferenceRepository
) : AuthenticationRepository {

    override suspend fun login(userContact: String): ResponseMapper<LoginResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.login(
                Login(
                    isdCode = ISD_INDIA,
                    mobileNumber = userContact
                )
            )
        )
    }

    override suspend fun validateOtp(userContact: String, otp: String): ResponseMapper<OtpResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.validateOtp(
                Otp(
                    isdCode = ISD_INDIA,
                    mobileNumber = userContact,
                    otp = otp
                )
            )
        ).apply {
            if (this is ApiEndResponse) {
                body.apply {
                    preferenceRepository.setAuthenticationToken(token)
                    preferenceRepository.setUserFhirId(userId)
                    preferenceRepository.setUserName(username)
                    preferenceRepository.setUserRoleId(roles[0])
                    preferenceRepository.setOrganizationFhirId(orgId)
                    preferenceRepository.setLocationFhirId(locationId[0].reference.substringAfter("/"))
                    preferenceRepository.setUserMobile(contactNumber.toLong())
                    preferenceRepository.setUserSessionID(sessionId)
                }
            }
        }
    }
}