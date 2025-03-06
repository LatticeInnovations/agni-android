package com.latticeonfhir.android.data.local.sharedpreferences

interface PreferenceStorage {

    /** Auth Token */
    var token: String

    /** User Data */
    var userFhirId: String
    var userName: String
    var userMobile: Long
    var userEmail: String
    var userRoleId: String
    var userRole: String
    var organizationFhirId: String
    var organization: String

    /** Room DB Encryption Key */
    var roomDBEncryptionKey: String

    /** Last Sync Time */
    var syncStatus: String
    var lastSyncTime: Long
    var lastPatientSyncTime: Long
    var lastRelationSyncTime: Long
    var lastPrescriptionSyncTime: Long
    var lastMedicationSyncTime: Long
    var lastMedicineDosageInstructionSyncTime: Long
    var lastScheduleSyncTime: Long
    var lastAppointmentSyncTime: Long
    var lastCVDSyncTime: Long
    var lastVitalSyncTime: Long

    var lastSymptomsSyncTime: Long

    var lastLabTestSyncTime: Long
    var lastMedicalRecordSyncTime: Long

    var lastSyncManufacturerRecord: Long

    fun clear()
}