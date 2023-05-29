package com.latticeonfhir.android.data.local.repository.preference

interface PreferenceRepository {

    /** Last Sync Patient */
    fun setLastSyncPatient(long: Long)
    fun getLastSyncPatient(): Long

    /** Last Sync Relation */
    fun setLastSyncRelation(long: Long)
    fun getLastSyncRelation(): Long

    /** Last Sync Prescription */
    fun setLastSyncPrescription(long: Long)
    fun getLastSyncPrescription(): Long

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

    /** OTP Attempts Timeout */
    fun setOtpAttemptTimeout(timeout: Long)
    fun getOtpAttemptTimeout(): Long

    /** Clear preferences */
    fun clearPreferences()
}