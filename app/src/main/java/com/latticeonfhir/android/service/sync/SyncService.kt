package com.latticeonfhir.android.service.sync

import android.content.Context
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import com.latticeonfhir.android.utils.network.CheckNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SyncService(
    private val context: Context,
    private val syncRepository: SyncRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository,
    private val fileSyncRepository: FileSyncRepository,
    private val symptomsAndDiagnosisRepository: SymptomsAndDiagnosisRepository
) {

    private lateinit var patientDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var scheduleDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var appointmentPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var prescriptionPatchJob: Deferred<ResponseMapper<Any>?>

    /**
     *
     *
     * Launcher
     *
     *
     * */

    internal suspend fun syncLauncher(logout: (Boolean, String) -> Unit) {
        if (CheckNetwork.isInternetAvailable(context)) {
            coroutineScope {
                awaitAll(
                    async {
                        uploadPatientAndScheduleJob(logout)
                    },
                    async {
                        patchPatient(logout)
                    },
                    async {
                        patchRelation(logout)
                    },
                    async {
                        patchPrescription(logout)
                    },
                    async {
                        downloadMedicationTiming(logout)
                    },
                    async {
                        uploadPatientLastUpdatedData(logout)
                    },
                    async {
                        uploadPrescriptionPhoto(logout)
                    },
                    async {
                        patchCVD(logout)
                    }, async {
                        patchLabTest(logout)
                    }, async {
                        patchMedRecord(logout)
                    }, async {
                        uploadLabAndMedPhoto(logout)
                    }, async {
                        patchSymDiag(logout)
                    }, async {
                        downloadVaccineManufacturer(logout)
                    }
                )
            }
        }
    }

    /**
     *
     * Upload patient and schedule
     * Asynchronously
     *
     * */

    private suspend fun uploadPatientAndScheduleJob(logout: (Boolean, String) -> Unit): Boolean {
        return coroutineScope {
            awaitAll(
                async {
                    uploadPatient(logout)
                },
                async {
                    uploadSchedule(logout)
                }
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse
            }.apply {
                if (this) {
                    downloadScheduleJob(logout)
                }
            }
        }
    }

    /**
     *
     * Download Schedule after Uploading Appointment and Patch Appointments
     * Asynchronously
     *
     * */

    private suspend fun downloadScheduleJob(logout: (Boolean, String) -> Unit) {
        return coroutineScope {
            awaitAll(
                async {
                    updateFhirIdsInAppointment(logout)
                },
                appointmentPatchJob
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse
            }.apply {
                if (this) {
                    scheduleDownloadJob = async {
                        downloadSchedule(logout)
                    }
                    downloadAppointmentJob(logout)
                }
            }
        }
    }

    /**
     *
     * Download Appointment after Downloading Schedule and Patients
     * Asynchronously
     *
     * */

    private suspend fun downloadAppointmentJob(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            awaitAll(
                patientDownloadJob,
                scheduleDownloadJob
            ).all { responseMapper -> responseMapper is ApiEndResponse }.apply {
                if (this) {
                    downloadAppointment(logout)
                }
            }
        }
    }

    /**
     *
     *
     * Upload Syncing
     *
     *
     * */

    /** Upload Patient */
    private suspend fun uploadPatient(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPersonPostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {
                CoroutineScope(Dispatchers.IO).apply {
                    launch {
                        updateFhirIdInRelation(logout)
                    }
                    patientDownloadJob = async {
                        downloadPatient(logout)
                    }
                }
            }
        }
    }

    /** Upload Relation */
    private suspend fun uploadRelation(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendRelatedPersonPostData(), logout)
    }

    /** Upload Schedule */
    private suspend fun uploadSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendSchedulePostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {
                appointmentPatchJob = CoroutineScope(Dispatchers.IO).async {
                    updateScheduleFhirIdInAppointmentPatch(logout)
                }
            }
        }
    }

    /** Upload Appointment */
    private suspend fun uploadAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAppointmentPostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {

                updateFhirIdInCVD(logout)
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInVital(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInPrescription(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInSymDiag(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInLabTest(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInMedical(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInImmunization(logout)
                }
            }
        }
    }

    /** Upload Form Prescription*/
    private suspend fun uploadPhotoPrescriptionData(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPhotoPrescriptionPostData(), logout)
    }

    /** Upload Photo Prescription*/
    private suspend fun uploadFormPrescriptionData(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendFormPrescriptionPostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInDispense(logout)
                }
            }
        }
    }

    /** Upload Patient Last Updated Data */
    private suspend fun uploadPatientLastUpdatedData(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPatientLastUpdatePostData(), logout)
    }

    /** Upload Prescription photos */
    private suspend fun uploadPrescriptionPhoto(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(fileSyncRepository.uploadFile(), logout)
    }

    /** Upload CVD */
    private suspend fun uploadCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendCVDPostData(), logout)
    }

    /** Upload SymDiag */

    private suspend fun uploadSymDiag(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendSymptomsAndDiagnosisPostData(), logout)
    }

    /** Upload Vital */
    private suspend fun uploadVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendVitalPostData(), logout)
    }

    /** Upload Photos Data */
    private suspend fun uploadLabAndMedPhoto(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(fileSyncRepository.uploadFile(), logout)
    }

    /** Upload LabTest */
    private suspend fun uploadLabTest(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendLabTestPostData(), logout)
    }

    /** Upload MedicalRecord */
    private suspend fun uploadMedicalRecord(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendMedRecordPostData(), logout)
    }

    /** Upload Dispense */
    private suspend fun uploadDispense(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendDispensePostData(), logout)
    }

    /** Upload Immunization */
    private suspend fun uploadImmunization(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendImmunizationPostData(), logout)
    }

    /**
     *
     *
     * Patch Syncing
     *
     *
     * */

    /** Patch Patient */
    private suspend fun patchPatient(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendPersonPatchData(), logout)
    }

    /** Patch Relation */
    private suspend fun patchRelation(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendRelatedPersonPatchData(), logout)
    }

    /** Patch Appointment */
    private suspend fun patchAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAppointmentPatchData(), logout)
    }

    /** Patch Prescription */
    internal suspend fun patchPrescription(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            prescriptionPatchJob = async {
                checkAuthenticationStatus(syncRepository.sendPrescriptionPhotoPatchData(), logout)
            }
        }
    }

    /** Patch CVD */
    private suspend fun patchCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendCVDPatchData(), logout)
    }

    /** Patch SymDiag */
    private suspend fun patchSymDiag(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendSymptomsAndDiagnosisPatchData(), logout)
    }

    /** Patch LabTest */
    private suspend fun patchLabTest(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendLabTestPatchData(), logout)
    }

    /** Patch Medical Record */
    private suspend fun patchMedRecord(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(
            syncRepository.sendMedRecordPatchData(), logout
        )
    }

    /** Delete Photo Prescription */
    private suspend fun deletePhotoPrescription(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.deletePrescriptionPhoto(), logout)
    }

    /** Delete Photo LabTest */
    private suspend fun deletePhotoLabTest(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.deleteLabTestPhoto(), logout)
    }

    /** Delete Photo Medical Record */
    private suspend fun deletePhotoMedicalRecord(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.deleteMedTestPhoto(), logout)
    }

    /**
     *
     *
     * Download Syncing
     *
     *
     * */

    /** Download Patient */
    private suspend fun downloadPatient(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListPatientData(0),
            logout
        )?.apply {
            if (this is ApiEndResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    downloadRelation(logout)
                }
            }
        }
    }

    /** Download Relation */
    private suspend fun downloadRelation(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertRelation(), logout)
    }

    /** Download Immunization And Recommendation */
    private suspend fun downloadImmunizationAndRecommendation(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertImmunization(patientId), logout)?.apply {
            if (this is ApiEmptyResponse || this is ApiEndResponse) {
                downloadPrescriptionPhoto(logout)
            }
        }
    }

    /** Download Schedule */
    private suspend fun downloadSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertSchedule(0), logout)
    }

    /** Download Appointment*/
    private suspend fun downloadAppointment(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            awaitAll(
                async {
                    checkAuthenticationStatus(syncRepository.getAndInsertAppointment(0), logout)
                },
               prescriptionPatchJob
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse || responseMapper is ApiEndResponse
            }.apply {
                if (this) {
                    downloadPatientLastUpdated(logout)
                    CoroutineScope(Dispatchers.IO).launch {
                        deletePhotoPrescription(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        deletePhotoLabTest(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        deletePhotoMedicalRecord(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadFormPrescription(null, logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadPhotoPrescription(null, logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadCVD(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadVitals(logout)
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        getAndInsertSymptoms()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        getAndInsertDiagnosis()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadSymDiag(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadLabAndMedicalRecordPhoto(logout)
                    }
                }
            }
        }
    }

    /** Download Form Prescription*/
    internal suspend fun downloadFormPrescription(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertFormPrescription(patientId),
            logout
        )?.apply {
            if (this is ApiEmptyResponse || this is ApiEndResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    downloadDispense(patientId, logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    downloadOTC(patientId, logout)
                }
            }
        }
    }

    /** Download Photo Prescription*/
    internal suspend fun downloadPhotoPrescription(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertPhotoPrescription(patientId),
            logout
        )?.apply {
            if (this is ApiEmptyResponse || this is ApiEndResponse) {
                downloadImmunizationAndRecommendation(patientId, logout)
            }
        }
    }

    /** Download Medication */
    internal suspend fun downloadMedication(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertMedication(0), logout)
    }

    /** Download Medication Timing */
    private suspend fun downloadMedicationTiming(logout: (Boolean, String) -> Unit) {
        if (preferenceRepository.getLastMedicineDosageInstructionSyncDate() == 0L) {
            checkAuthenticationStatus(syncRepository.getMedicineTime(), logout)
        }
    }

    private suspend fun downloadVaccineManufacturer(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertManufacturer(), logout)
    }

    /** Download Patient Last Updated */
    private suspend fun downloadPatientLastUpdated(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertPatientLastUpdatedData(), logout)
    }

    /** Download Patient Last Updated */
    private suspend fun downloadPrescriptionPhoto(logout: (Boolean, String) -> Unit) {
        fileSyncRepository.startDownload(GenericTypeEnum.PRESCRIPTION_PHOTO, logout)
    }

    /** Download CVD*/
    private suspend fun downloadCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCVD(0), logout)
    }

    /** Download SymptomsDiag*/
    private suspend fun downloadSymDiag(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListSymptomsAndDiagnosisData(0),
            logout
        )
    }

    /** Download Vitals*/
    private suspend fun downloadVitals(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertListVitalData(0), logout)
    }

    /** Download Lab And Medical Record Photo */
    private suspend fun downloadLabAndMedicalRecordPhoto(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            awaitAll(async {
                downloadLabTest(logout)
            }, async {
                downloadMedicalRecord(logout)
            }).all { responseMapper ->
                responseMapper is ApiEndResponse
            }.apply {
                if (this) {
                    fileSyncRepository.startDownload(GenericTypeEnum.PHOTO_DOWNLOAD, logout)
                }
            }
        }
    }

    /** Download LabTest*/
    private suspend fun downloadLabTest(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListLabTestData(0),
            logout
        )
    }

    /** Download MedicalRecord*/
    private suspend fun downloadMedicalRecord(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListMedicalRecordData(0),
            logout
        )
    }

    /** Download Dispense*/
    internal suspend fun downloadDispense(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertDispense(patientId),
            logout
        )
    }

    /** Download OTC Dispense*/
    internal suspend fun downloadOTC(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertOTC(patientId),
            logout
        )
    }

    /**
     *
     *
     * Update FHIR ID in Generic Entity
     *
     *
     * */

    /** Update Patient FHIR ID in Relation */
    private suspend fun updateFhirIdInRelation(logout: (Boolean, String) -> Unit) {
        genericRepository.updateRelationFhirId()
        /** Start Relation Worker */
        uploadRelation(logout)
    }

    /** Update Schedule and Patient FHIR ID in Appointment */
    private suspend fun updateFhirIdsInAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAppointmentFhirIds()
        /** Upload Appointment */
        return uploadAppointment(logout)
    }

    /** Update Schedule FHIR ID in Appointment Patch */
    private suspend fun updateScheduleFhirIdInAppointmentPatch(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAppointmentFhirIdInPatch()
        /** Patch Appointment */
        return patchAppointment(logout)
    }

    /** Update Appointment FHIR ID in Prescription */
    private suspend fun updateFhirIdInPrescription(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updatePrescriptionFhirId()
        /** Upload Prescription */
        CoroutineScope(Dispatchers.IO).launch {
            uploadFormPrescriptionData(logout)
        }
        return uploadPhotoPrescriptionData(logout)
    }

    /** Update Appointment FHIR ID in CVD */
    private suspend fun updateFhirIdInCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateCVDFhirIds()
        /** Upload Prescription */
        return uploadCVD(logout)
    }

    private suspend fun updateFhirIdInVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateVitalFhirId()
        return uploadVital(logout)
    }

    private suspend fun updateFhirIdInSymDiag(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateSymDiagFhirId()
        return uploadSymDiag(logout)
    }

    private suspend fun updateFhirIdInLabTest(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateLabTestFhirId()
        return uploadLabTest(logout)
    }

    private suspend fun updateFhirIdInMedical(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateMedRecordFhirId()
        return uploadMedicalRecord(logout)
    }

    /** Update Appointment FHIR ID in Dispense */
    private suspend fun updateFhirIdInDispense(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateDispenseFhirId()
        /** Upload Dispense */
        return uploadDispense(logout)
    }

    private suspend fun updateFhirIdInImmunization(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateImmunizationFhirId()
        return uploadImmunization(logout)
    }

    /** Check Session Expiry and Authorization */
    private fun checkAuthenticationStatus(
        responseMapper: ResponseMapper<Any>,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return if (responseMapper is ApiErrorResponse) {
            if (responseMapper.errorMessage == ErrorConstants.SESSION_EXPIRED || responseMapper.errorMessage == ErrorConstants.UNAUTHORIZED) {
                logout(true, responseMapper.errorMessage)
            } else logout(false, responseMapper.errorMessage)
            null
        } else {
            responseMapper
        }
    }

    private suspend fun getAndInsertSymptoms() {
        if (symptomsAndDiagnosisRepository.getSymptoms().isEmpty()) {
            symptomsAndDiagnosisRepository.insertSymptoms()
        }

    }

    private suspend fun getAndInsertDiagnosis() {
        if (symptomsAndDiagnosisRepository.getDiagnosis().isEmpty()) {
            symptomsAndDiagnosisRepository.insertDiagnosis()
        }
    }
}