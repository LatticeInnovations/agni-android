package com.latticeonfhir.android.service.workmanager.request

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.service.workmanager.utils.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.utils.RepeatInterval
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.utils.defaultRetryConfiguration
import com.latticeonfhir.android.service.workmanager.workers.download.medication.MedicationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage.MedicineDosageDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.prescription.PrescriptionDownloadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.download.prescription.PrescriptionDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.prescription.PrescriptionUploadSyncWorker.Companion.PRESCRIPTION_UPLOAD_PROGRESS
import com.latticeonfhir.android.service.workmanager.workers.upload.prescription.PrescriptionUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.constants.ErrorConstants.ERROR_MESSAGE
import com.latticeonfhir.android.utils.constants.Id
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class WorkRequestBuilders(
    private val applicationContext: Context,
    private val genericRepository: GenericRepository,
    private val patientRepository: PatientRepository
) {

    /**
     *
     *
     * Upload Workers
     *
     *
     *
     * */

    /** Patient Upload Post Sync Worker */
    internal suspend fun uploadPatientWorker(error: (Boolean, String) -> Unit) {
        Timber.d("Worker Uncle Status at Top")
        //Upload Worker
        Sync.periodicSync<PatientUploadSyncWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsgFromServer
                )
                val value = workInfo.progress.getInt(PatientUploadSyncWorker.PatientUploadProgress, 0)
                if (value == 100) {
                    /** Update Fhir Id in Generic Entity */
                    updateFhirIdInRelation { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
                    }

                    updateFhirIdInPrescription { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
                    }
                }
                if (workInfo.state == WorkInfo.State.ENQUEUED) {
                    downloadPatientWorker { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
                    }
                }
            }
        }
    }

    /** Upload Relation Post Sync Worker */
    private suspend fun uploadRelationWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<RelationUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else {
                    val progress = workInfo.progress
                    val value = progress.getInt(RelationUploadSyncWorker.RelationUploadProgress, 0)
                    if (value == 100) {
                        /** Handle Progress Based Download WorkRequests Here */
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        downloadPatientWorker { errorReceived, errorMsg ->
                            error(errorReceived, errorMsg)
                        }
                    }
                }
            }
        }
    }

    /** Upload Prescription Data */
    private suspend fun uploadPrescriptionSyncWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<PrescriptionUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else {
                    val progress = workInfo.progress
                    val value = progress.getInt(PRESCRIPTION_UPLOAD_PROGRESS, 0)
                    if (value == 100) {
                        /** Handle Progress Based Download WorkRequests Here */
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        downloadPatientWorker { errorReceived, errorMsg ->
                            error(errorReceived, errorMsg)
                        }
                    }
                }
            }
        }
    }

    /**
     *
     *
     * Download Workers
     *
     *
     *
     * */

    /** Patient Download Sync Worker */
    private suspend fun downloadPatientWorker(error: (Boolean, String) -> Unit) {
        /** Download Worker */
        Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo?.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            } else if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                downloadRelationWorker { errorReceived, errorMsg ->
                    error(errorReceived, errorMsg)
                }
            }
        }
    }

    /** Relation Download Sync Worker */
    private suspend fun downloadRelationWorker(error: (Boolean, String) -> Unit) {
        //Download Worker
        Sync.oneTimeSync<RelationDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    /** Handle Success Here */
                }
            }
        }
    }

    /** Download Prescription Data */
    internal suspend fun downloadPrescriptionWorker(
        patientFhirId: String,
        error: (Boolean, String) -> Unit
    ) {
        /** Download Worker */
        PrescriptionDownloadSyncWorker.patientFhirId = patientFhirId
        Sync.oneTimeSync<PrescriptionDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {

                }
            }
        }
    }

    /** Medication Worker  */
    internal suspend fun setMedicationWorker(error: (Boolean, String) -> Unit) {
        Sync.oneTimeSync<MedicationDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    /** Handle Success Here */
                }
            }
        }
    }

    /** Medication Dosage Worker  */
    internal suspend fun setMedicationDosageWorker(error: (Boolean, String) -> Unit) {
        Sync.oneTimeSync<MedicineDosageDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                    if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsg
                    )
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    /** Handle Success Here */
                }
            }
        }
    }

    /**
     *
     *
     * Patch Workers
     *
     *
     * */

    //Patient Patch Sync
    internal suspend fun setPatientPatchWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.periodicSync<PatientPatchUploadSyncWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsgFromServer
                )
                val value = workInfo.progress.getInt(PatientPatchUploadSyncWorker.PatientPatchUpload, 0)
                if (value == 100) {
                    /** Handle Progress Based Download WorkRequests Here */
                }
            }
        }
    }

    //Relation Patch Sync
    internal suspend fun setRelationPatchWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.periodicSync<RelationPatchUploadSyncWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsgFromServer
                )
                val value = workInfo.progress.getInt(RelationPatchUploadSyncWorker.RelationPatchUpload, 0)
                if (value == 100) {
                    /** Handle Progress Based Download WorkRequests Here */
                }
            }
        }
    }

    /**
     *
     *
     * Update in Generic Entity Methods
     *
     *
     * */

    private suspend fun updateFhirIdInRelation(error: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            genericRepository.getNonSyncedPostRelations().forEach { genericEntity ->
                val existingMap = genericEntity.payload.fromJson<MutableMap<String, Any>>()
                    .mapToObject(RelatedPersonResponse::class.java)
                if (existingMap != null) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = genericEntity.patientId,
                        entity = existingMap.copy(
                            id = patientRepository.getPatientById(existingMap.id)[0].fhirId
                                ?: Id.EMPTY_FHIR_ID,
                            relationship = existingMap.relationship.map { relationship ->
                                relationship.copy(
                                    relativeId = patientRepository.getPatientById(relationship.relativeId)[0].fhirId
                                        ?: Id.EMPTY_FHIR_ID
                                )
                            }
                        ),
                        typeEnum = GenericTypeEnum.RELATION,
                        replaceEntireRow = true
                    )
                }
            }
            /** Start Relation Worker */
            uploadRelationWorker { errorReceived, errorMsg ->
                error(errorReceived, errorMsg)
            }
        }
    }

    private fun updateFhirIdInPrescription(error: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            genericRepository.getNonSyncedPostRelations().forEach { genericEntity ->
                val existingMap = genericEntity.payload.fromJson<MutableMap<String, Any>>()
                    .mapToObject(PrescriptionResponse::class.java)
                if (existingMap != null) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = genericEntity.patientId,
                        entity = existingMap.copy(
                            patientFhirId = patientRepository.getPatientById(existingMap.patientFhirId)[0].fhirId
                                ?: Id.EMPTY_FHIR_ID
                        ),
                        typeEnum = GenericTypeEnum.PRESCRIPTION,
                        replaceEntireRow = true
                    )
                }
            }
            /** Start Prescription Worker */
            uploadPrescriptionSyncWorker { errorReceived, errorMsg ->
                error(errorReceived, errorMsg)
            }
        }
    }
}