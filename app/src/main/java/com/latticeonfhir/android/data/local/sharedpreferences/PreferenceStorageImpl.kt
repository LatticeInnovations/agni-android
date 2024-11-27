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
    override var roomDBEncryptionKey by StringPreference(
        sharedPreferences,
        PREF_ROOM_ENCRYPTION_KEY,
        ""
    )
    override var syncStatus by StringPreference(
        sharedPreferences,
        PREF_SYNC_STATUS,
        ""
    )
    override var lastSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_SYNC_TIME,
        0L
    )
    override var lastPatientSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_PATIENT_SYNC_TIME,
        0L
    )
    override var lastRelationSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_RELATION_SYNC_TIME,
        0L
    )
    override var lastPrescriptionSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_PRESCRIPTION_SYNC_TIME,
        0L
    )
    override var lastMedicationSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_MEDICATION_SYNC_TIME,
        0L
    )
    override var lastMedicineDosageInstructionSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_MEDICINE_DOSAGE_INSTRUCTION_SYNC_TIME,
        0L
    )
    override var lastScheduleSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_SCHEDULE_SYNC_TIME,
        0L
    )
    override var lastAppointmentSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_APPOINTMENT_SYNC_TIME,
        0L
    )
    override var lastCVDSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_CVD_SYNC_TIME,
        0L
    )
    override var lastLabTestSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_LAB_TEST_SYNC_TIME,
        0L
    )
    override var lastMedicalRecordSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_MEDICAL_RECORD_SYNC_TIME,
        0L
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

        const val PREF_ROOM_ENCRYPTION_KEY = "pref_room_encryption_key"

        const val PREF_SYNC_STATUS = "pref_sync_status"
        const val PREF_LAST_SYNC_TIME = "pref_last_sync_time"
        const val PREF_LAST_PATIENT_SYNC_TIME = "pref_last_patient_sync_time"
        const val PREF_LAST_RELATION_SYNC_TIME = "pref_last_relation_sync_time"
        const val PREF_LAST_PRESCRIPTION_SYNC_TIME = "pref_last_prescription_sync_time"
        const val PREF_LAST_MEDICATION_SYNC_TIME = "pref_last_medication_sync_time"
        const val PREF_LAST_MEDICINE_DOSAGE_INSTRUCTION_SYNC_TIME =
            "pref_last_medication_timing_sync_time"
        const val PREF_LAST_SCHEDULE_SYNC_TIME = "pref_last_schedule_sync_time"
        const val PREF_LAST_APPOINTMENT_SYNC_TIME = "pref_last_appointment_sync_time"
        const val PREF_LAST_CVD_SYNC_TIME = "pref_last_cvd_sync_time"

        const val PREF_LAST_LAB_TEST_SYNC_TIME = "pref_last_lab_test_sync_time"
        const val PREF_LAST_MEDICAL_RECORD_SYNC_TIME = "pref_last_medical_record_sync_time"
    }
}