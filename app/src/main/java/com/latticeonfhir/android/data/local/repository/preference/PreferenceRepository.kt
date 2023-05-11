package com.latticeonfhir.android.data.local.repository.preference

interface PreferenceRepository {

    /** Last Updated Date */
    fun setLastUpdatedDate(long: Long)
    fun getLastUpdatedDate(): Long

    /** User Data */
    fun setUserName(userName: String)
    fun getUserName(): String
    fun setUserRole(userRole: String)
    fun getUserRole(): String
    fun setUserMobile(userMobile: Long)
    fun getUserMobile(): Long
    fun setUserEmail(userEmail: String)
    fun getUserEmail(): String

    /** Authentication Token */
    fun setAuthenticationToken(authToken: String)
    fun getAuthenticationToken(): String

    /** Clear preferences */
    fun clearPreferences()
}