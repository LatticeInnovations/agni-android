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
    var lastPatientSyncTime: Long
    var lastRelationSyncTime: Long
    var lastPrescriptionSyncTime: Long
    var lastMedicationSyncTime: Long
    var lastMedicineDosageInstructionSyncTime: Long

    fun clear()
}