package com.latticeonfhir.android.data.local.sharedpreferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.latticeonfhir.android.utils.sharedpreference.BooleanPreference
import com.latticeonfhir.android.utils.sharedpreference.LongPreference
import com.latticeonfhir.android.utils.sharedpreference.StringPreference

class PreferenceStorageImpl(private val sharedPreferences: SharedPreferences) : PreferenceStorage {

    override var loginFlag by BooleanPreference(sharedPreferences, PREF_LOGIN_FLAG, false)
    override var defaultLanguage by BooleanPreference(
        sharedPreferences,
        PREF_DEFAULT_LANGUAGE,
        false
    )
    override var token by StringPreference(sharedPreferences, PREF_TOKEN, "")
    override var empCode by StringPreference(sharedPreferences, PREF_EMP_CODE, "")
    override var username by StringPreference(sharedPreferences, PREF_USERNAME, "")
    override var userId by StringPreference(sharedPreferences, PREF_USERID, "")
    override var usertype by LongPreference(sharedPreferences, PREF_USERTYPE, 3)
    override var hospitalId by StringPreference(sharedPreferences, PREF_HOSPITAL, "")
    override var uuid by StringPreference(sharedPreferences, PREF_UUID, "")
    override var mobile by StringPreference(sharedPreferences, PREF_MOBILE, "")
    override var pin by StringPreference(sharedPreferences, PREF_PIN, "")

    override fun clear() {
        sharedPreferences.edit {
            clear()
            commit()
        }
    }

    companion object {
        const val PREFS_NAME = "fhir_android"
        const val PREF_LOGIN_FLAG = "pref_login_flag"
        const val PREF_DEFAULT_LANGUAGE = "pref_default_lang"
        const val PREF_TOKEN = "pref_token"
        const val PREF_EMP_CODE = "pref_emp"
        const val PREF_USERNAME = "pref_name"
        const val PREF_USERID = "pref_user_id"
        const val PREF_UUID = "pref_uuid"
        const val PREF_USERTYPE = "pref_type"
        const val PREF_MOBILE = "pref_mobile"
        const val PREF_HOSPITAL = "pref_hospital_id"
        const val PREF_PIN = "pref_pin"
    }
}