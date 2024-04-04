package com.latticeonfhir.android.data.local.repository.preference

interface PreferenceRepository {
    /** User Data */
    fun setUserFhirId(userFhirId: String)
    fun getUserFhirId(): String
    fun setUserName(userName: String)
    fun getUserName(): String
    fun setUserMobile(userMobile: Long)
    fun getUserMobile(): Long
    fun setUserEmail(userEmail: String)
    fun getUserEmail(): String
    fun setUserRoleId(userRoleId: String)
    fun getUserRoleId(): String
    fun setUserRole(userRole: String)
    fun getUserRole(): String
    fun setOrganizationFhirId(organizationFhirId: String)
    fun getOrganizationFhirId(): String
    fun setOrganization(organization: String)
    fun getOrganization(): String
    fun setLocationFhirId(locationFhirId: String)
    fun getLocationFhirId(): String

    /** Authentication Token */
    fun setAuthenticationToken(authToken: String)
    fun getAuthenticationToken(): String

    /** RoomDB EncryptionKey */
    fun setRoomDBEncryptionKey(encryptionKey: String)
    fun getRoomDBEncryptionKey(): String

    /** Reset Authentication Token */
    fun resetAuthenticationToken()

    /** Clear preferences */
    fun clearPreferences()
}