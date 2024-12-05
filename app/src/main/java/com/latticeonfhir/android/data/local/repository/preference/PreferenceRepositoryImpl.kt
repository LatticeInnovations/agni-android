package com.latticeonfhir.android.data.local.repository.preference

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage) :
    PreferenceRepository {
    override fun setSyncStatus(status: String) {
        preferenceStorage.syncStatus = status
    }

    override fun getSyncStatus() = preferenceStorage.syncStatus
    override fun setLastSyncTime(long: Long) {
        preferenceStorage.lastSyncTime = long
    }

    override fun getLastSyncTime() = preferenceStorage.lastSyncTime

    override fun setLastSyncPatient(long: Long) {
        preferenceStorage.lastPatientSyncTime = long
    }

    override fun getLastSyncPatient() = preferenceStorage.lastPatientSyncTime

    override fun setLastSyncRelation(long: Long) {
        preferenceStorage.lastRelationSyncTime = long
    }

    override fun getLastSyncRelation() = preferenceStorage.lastRelationSyncTime

    override fun setLastSyncPrescription(long: Long) {
        preferenceStorage.lastPrescriptionSyncTime = long
    }

    override fun getLastSyncPrescription() = preferenceStorage.lastPrescriptionSyncTime

    override fun setLastMedicationSyncDate(long: Long) {
        preferenceStorage.lastMedicationSyncTime = long
    }

    override fun getLastMedicationSyncDate(): Long = preferenceStorage.lastMedicationSyncTime

    override fun setLastMedicineDosageInstructionSyncDate(long: Long) {
        preferenceStorage.lastMedicineDosageInstructionSyncTime = long
    }

    override fun setLastSyncSchedule(long: Long) {
        preferenceStorage.lastScheduleSyncTime = long
    }

    override fun getLastSyncSchedule() = preferenceStorage.lastScheduleSyncTime

    override fun setLastSyncAppointment(long: Long) {
        preferenceStorage.lastAppointmentSyncTime = long
    }

    override fun getLastSyncAppointment() = preferenceStorage.lastAppointmentSyncTime

    override fun setLastSyncCVD(long: Long) {
        preferenceStorage.lastCVDSyncTime = long
    }

    override fun getLastSyncCVD() = preferenceStorage.lastCVDSyncTime

    override fun setLastSyncVital(long: Long) {
        preferenceStorage.lastVitalSyncTime = long

    }
    override fun getLastSyncVital() = preferenceStorage.lastVitalSyncTime

    override fun setLastSyncSymDiag(long: Long) {
        preferenceStorage.lastSymptomsSyncTime = long
    }

    override fun getLastSyncSymDiag() = preferenceStorage.lastSymptomsSyncTime

    override fun setLastSyncLabTest(long: Long) {
        preferenceStorage.lastLabTestSyncTime = long
    }

    override fun getLastSyncLabTest() = preferenceStorage.lastLabTestSyncTime


    override fun setLastSyncMedicalRecord(long: Long) {
        preferenceStorage.lastMedicalRecordSyncTime = long
    }

    override fun getLastSyncMedicalRecord() = preferenceStorage.lastMedicalRecordSyncTime

    override fun getLastMedicineDosageInstructionSyncDate() =
        preferenceStorage.lastMedicineDosageInstructionSyncTime

    override fun setUserFhirId(userFhirId: String) {
        preferenceStorage.userFhirId = userFhirId
    }

    override fun getUserFhirId() = preferenceStorage.userFhirId

    override fun setUserName(userName: String) {
        preferenceStorage.userName = userName
    }

    override fun getUserName() = preferenceStorage.userName

    override fun setUserMobile(userMobile: Long) {
        preferenceStorage.userMobile = userMobile
    }

    override fun getUserMobile() = preferenceStorage.userMobile

    override fun setUserEmail(userEmail: String) {
        preferenceStorage.userEmail = userEmail
    }

    override fun getUserEmail() = preferenceStorage.userEmail

    override fun setUserRoleId(userRoleId: String) {
        preferenceStorage.userRoleId = userRoleId
    }

    override fun getUserRoleId() = preferenceStorage.userRoleId

    override fun setUserRole(userRole: String) {
        preferenceStorage.userRole = userRole
    }

    override fun getUserRole() = preferenceStorage.userRole

    override fun setOrganizationFhirId(organizationFhirId: String) {
        preferenceStorage.organizationFhirId = organizationFhirId
    }

    override fun getOrganizationFhirId() = preferenceStorage.organizationFhirId

    override fun setOrganization(organization: String) {
        preferenceStorage.organization = organization
    }

    override fun getOrganization() = preferenceStorage.organization

    override fun setAuthenticationToken(authToken: String) {
        preferenceStorage.token = authToken
    }

    override fun getAuthenticationToken() = preferenceStorage.token

    override fun setRoomDBEncryptionKey(encryptionKey: String) {
        preferenceStorage.roomDBEncryptionKey = encryptionKey
    }

    override fun getRoomDBEncryptionKey() = preferenceStorage.roomDBEncryptionKey

    override fun resetAuthenticationToken() {
        preferenceStorage.token = ""
    }

    override fun clearPreferences() {
        val roomDBEncryptionKey = preferenceStorage.roomDBEncryptionKey
        preferenceStorage.clear()
        preferenceStorage.roomDBEncryptionKey = roomDBEncryptionKey
    }
}