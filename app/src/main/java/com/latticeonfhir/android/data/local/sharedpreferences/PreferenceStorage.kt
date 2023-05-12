package com.latticeonfhir.android.data.local.sharedpreferences

interface PreferenceStorage {

    /** Auth Token */
    var token: String

    /** User Data */
    var userName: String
    var userRole: String
    var userMobile: Long
    var userEmail: String

    /** Room DB Encryption Key */
    var roomDBEncryptionKey: String

    /** Last Sync Time */
    var lastUpdatedTime: Long

    /** Max Attempts of OTP Timeout */
    var maxOtpAttemptTimeout: Long

    fun clear()
}