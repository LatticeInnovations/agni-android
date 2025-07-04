package com.heartcare.agni.data.local.repository.preference

interface PreferenceRepository {

    /** Last Sync Status */
    fun setSyncStatus(status: String)
    fun getSyncStatus(): String

    /** Last Sync Time Overall */
    fun setLastSyncTime(long: Long)
    fun getLastSyncTime(): Long

    /** Last Sync Patient */
    fun setLastSyncPatient(long: Long)
    fun getLastSyncPatient(): Long

    /** Last Sync Relation */
    fun setLastSyncRelation(long: Long)
    fun getLastSyncRelation(): Long

    /** Last Sync Prescription */
    fun setLastSyncPrescription(long: Long)
    fun getLastSyncPrescription(): Long

    /** Last Medication Sync Date */
    fun setLastMedicationSyncDate(long: Long)
    fun getLastMedicationSyncDate(): Long

    /** Last Medicine Dosage Instruction Sync Date */
    fun setLastMedicineDosageInstructionSyncDate(long: Long)
    fun getLastMedicineDosageInstructionSyncDate(): Long

    /** Last Sync Schedule */
    fun setLastSyncSchedule(long: Long)
    fun getLastSyncSchedule(): Long

    /** Last Sync Appointment */
    fun setLastSyncAppointment(long: Long)
    fun getLastSyncAppointment(): Long

    /** Last Sync CVD */
    fun setLastSyncCVD(long: Long)
    fun getLastSyncCVD(): Long

    /** Last Sync Vital */
    fun setLastSyncVital(long: Long)
    fun getLastSyncVital(): Long

    /** Last Sync Symptoms And Diagnosis */
    fun setLastSyncSymDiag(long: Long)
    fun getLastSyncSymDiag(): Long

    /** Last Sync Lab Test */
    fun setLastSyncLabTest(long: Long)
    fun getLastSyncLabTest(): Long

    /** Last Sync Medical Record */
    fun setLastSyncMedicalRecord(long: Long)
    fun getLastSyncMedicalRecord(): Long

    /** Last Sync Manufacturer Record */
    fun setLastSyncManufacturerRecord(long: Long)
    fun getLastSyncManufacturerRecord(): Long

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