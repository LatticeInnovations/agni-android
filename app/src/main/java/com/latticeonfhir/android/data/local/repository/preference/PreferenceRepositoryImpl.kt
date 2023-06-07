package com.latticeonfhir.android.data.local.repository.preference

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage): PreferenceRepository {

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

    override fun getLastMedicineDosageInstructionSyncDate() = preferenceStorage.lastMedicineDosageInstructionSyncTime

    override fun setUserName(userName: String) {
        preferenceStorage.userName = userName
    }

    override fun getUserName() = preferenceStorage.userName

    override fun setUserRole(userRole: String) {
        preferenceStorage.userRole = userRole
    }

    override fun getUserRole() = preferenceStorage.userRole

    override fun setUserMobile(userMobile: Long) {
        preferenceStorage.userMobile = userMobile
    }

    override fun getUserMobile() = preferenceStorage.userMobile

    override fun setUserEmail(userEmail: String) {
        preferenceStorage.userEmail = userEmail
    }

    override fun getUserEmail() = preferenceStorage.userEmail

    override fun setAuthenticationToken(authToken: String) {
        preferenceStorage.token = authToken
    }

    override fun getAuthenticationToken() = preferenceStorage.token

    override fun setOtpAttemptTimeout(timeout: Long) {
        preferenceStorage.maxOtpAttemptTimeout = timeout
    }

    override fun getOtpAttemptTimeout() = preferenceStorage.maxOtpAttemptTimeout

    override fun setRoomDBEncryptionKey(encryptionKey: String) {
        preferenceStorage.roomDBEncryptionKey = encryptionKey
    }

    override fun getRoomDBEncryptionKey() = preferenceStorage.roomDBEncryptionKey

    override fun clearPreferences() {
        preferenceStorage.clear()
    }
}