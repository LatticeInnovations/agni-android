package com.latticeonfhir.android.data.local.sharedpreferences

interface PreferenceStorage {
    var loginFlag: Boolean
    var defaultLanguage: Boolean
    var token: String
    var empCode: String
    var username: String
    var userId: String
    var usertype: Long
    var hospitalId: String
    var uuid: String
    var mobile: String
    var pin: String

    fun clear()
}