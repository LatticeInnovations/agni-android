package com.latticeonfhir.android.data.local.repository.preference

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage) :
    PreferenceRepository {
    override fun setUserFhirId(userFhirId: String) {
        preferenceStorage.userFhirId = userFhirId
    }

    override fun getUserFhirId() = preferenceStorage.userFhirId

    override fun setUserName(userName: String) {
        preferenceStorage.userName = userName
    }

    override fun getUserName() = preferenceStorage.userName

    override fun setUserMobile(userMobile: Long) {
        preferenceStorage.userMobile = userMobile
    }

    override fun getUserMobile() = preferenceStorage.userMobile

    override fun setUserEmail(userEmail: String) {
        preferenceStorage.userEmail = userEmail
    }

    override fun getUserEmail() = preferenceStorage.userEmail

    override fun setUserRoleId(userRoleId: String) {
        preferenceStorage.userRoleId = userRoleId
    }

    override fun getUserRoleId() = preferenceStorage.userRoleId

    override fun setUserRole(userRole: String) {
        preferenceStorage.userRole = userRole
    }

    override fun getUserRole() = preferenceStorage.userRole

    override fun setOrganizationFhirId(organizationFhirId: String) {
        preferenceStorage.organizationFhirId = organizationFhirId
    }

    override fun getOrganizationFhirId() = preferenceStorage.organizationFhirId

    override fun setOrganization(organization: String) {
        preferenceStorage.organization = organization
    }

    override fun getOrganization() = preferenceStorage.organization
    override fun setLocationFhirId(locationFhirId: String) {
        preferenceStorage.locationFhirId = locationFhirId
    }

    override fun getLocationFhirId() = preferenceStorage.locationFhirId

    override fun setAuthenticationToken(authToken: String) {
        preferenceStorage.token = authToken
    }

    override fun getAuthenticationToken() = preferenceStorage.token

    override fun setRoomDBEncryptionKey(encryptionKey: String) {
        preferenceStorage.roomDBEncryptionKey = encryptionKey
    }

    override fun getRoomDBEncryptionKey() = preferenceStorage.roomDBEncryptionKey

    override fun resetAuthenticationToken() {
        preferenceStorage.token = ""
    }

    override fun clearPreferences() {
        val roomDBEncryptionKey = preferenceStorage.roomDBEncryptionKey
        preferenceStorage.clear()
        preferenceStorage.roomDBEncryptionKey = roomDBEncryptionKey
    }
}