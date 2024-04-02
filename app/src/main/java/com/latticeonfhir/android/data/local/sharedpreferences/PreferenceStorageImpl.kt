package com.latticeonfhir.android.data.local.sharedpreferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.latticeonfhir.android.utils.sharedpreference.LongPreference
import com.latticeonfhir.android.utils.sharedpreference.StringPreference

class PreferenceStorageImpl(private val sharedPreferences: SharedPreferences) : PreferenceStorage {

    override var token by StringPreference(sharedPreferences, PREF_TOKEN, "")
    override var userFhirId by StringPreference(sharedPreferences, PREF_USER_FHIR_ID, "")
    override var userName by StringPreference(sharedPreferences, PREF_USER_NAME, "")
    override var userMobile by LongPreference(sharedPreferences, PREF_USER_MOBILE, 0L)
    override var userEmail by StringPreference(sharedPreferences, PREF_USER_EMAIL, "")
    override var userRoleId by StringPreference(sharedPreferences, PREF_USER_ROLE_ID, "")
    override var userRole by StringPreference(sharedPreferences, PREF_USER_ROLE, "")
    override var organizationFhirId by StringPreference(
        sharedPreferences,
        PREF_ORGANIZATION_FHIR_ID,
        ""
    )
    override var organization by StringPreference(sharedPreferences, PREF_ORGANIZATION, "")
    override var locationFhirId by StringPreference(
        sharedPreferences,
        PREF_LOCATION_FHIR_ID,
        ""
    )
    override var roomDBEncryptionKey by StringPreference(
        sharedPreferences,
        PREF_ROOM_ENCRYPTION_KEY,
        ""
    )
    override fun clear() {
        sharedPreferences.edit {
            clear()
            commit()
        }
    }

    companion object {
        const val PREFS_NAME = "fhir_android"

        const val PREF_TOKEN = "pref_token"

        const val PREF_USER_FHIR_ID = "pref_user_fhir_id"
        const val PREF_USER_NAME = "pref_user_name"
        const val PREF_USER_MOBILE = "pref_user_mobile"
        const val PREF_USER_EMAIL = "pref_user_email"
        const val PREF_USER_ROLE_ID = "pref_user_role_id"
        const val PREF_USER_ROLE = "pref_user_role"
        const val PREF_ORGANIZATION_FHIR_ID = "pref_organization_fhir_id"
        const val PREF_ORGANIZATION = "pref_organization"
        const val PREF_LOCATION_FHIR_ID = "pref_location_fhir_id"

        const val PREF_ROOM_ENCRYPTION_KEY = "pref_room_encryption_key"
    }
}