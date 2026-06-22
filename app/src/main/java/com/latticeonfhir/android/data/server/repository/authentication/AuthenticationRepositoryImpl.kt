package com.latticeonfhir.android.data.server.repository.authentication

import androidx.core.text.isDigitsOnly
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.FacilityResponse
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
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
                saveUserDetailsAndGetFacility(body)
            }
        }
    }

    override suspend fun saveUserDetailsAndGetFacility(
        body: TokenResponse
    ): ResponseMapper<FacilityResponse> {
        preferenceRepository.setAuthenticationToken(body.token)
        preferenceRepository.setUserFhirId(body.userId)
        preferenceRepository.setUserName(body.name)
        preferenceRepository.setUserRoleId(body.role)
        preferenceRepository.setUserRole(body.roleName)
        preferenceRepository.setOrganizationFhirId(body.orgId)
        if (body.contact.isDigitsOnly()){
            preferenceRepository.setUserMobile(body.contact.toLong())
        } else {
            preferenceRepository.setUserEmail(body.contact)
        }

        return getFacilityDetails(body.orgId)
    }

    private suspend fun getFacilityDetails(
        facilityId: String
    ): ResponseMapper<FacilityResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.getFacilityDetails(
                facilityId = facilityId
            )
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setFacilityDetails(body)
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