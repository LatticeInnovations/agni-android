package com.latticeonfhir.android.service.workmanager.request

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
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
import com.latticeonfhir.android.service.workmanager.workers.trigger.TriggerWorkerImpl
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class WorkRequestBuilders(
    private val applicationContext: Context,
    private val genericRepository: GenericRepository
) {

    /**
     *
     *
     * Upload Workers
     *
     *
     *
     * */

    internal suspend fun setPeriodicTriggerWorker(error: (Boolean, String) -> Unit) {
        Sync.periodicSync<TriggerWorkerImpl>(
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
                if (workInfo.state == WorkInfo.State.ENQUEUED) {
                    uploadPatientWorker { errorReceived, errorMsg ->
                        error(
                            errorReceived,
                            errorMsg
                        )
                    }

                    setPatientPatchWorker { errorReceived, errorMsg ->
                        error(
                            errorReceived,
                            errorMsg
                        )
                    }

                    setRelationPatchWorker { errorReceived, errorMsg ->
                        error(
                            errorReceived,
                            errorMsg
                        )
                    }
                }
            }
        }
    }

    /** Patient Upload Post Sync Worker */
    internal suspend fun uploadPatientWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<PatientUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                    if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsgFromServer
                    )
                } else {
                    if (workInfo.progress.getInt(
                            PatientUploadSyncWorker.PatientUploadProgress,
                            0
                        ) == 100
                    ) {
                        /** Handle Sync Progress Here */
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        /** Update Fhir Id in Generic Entity */
                        CoroutineScope(Dispatchers.IO).launch {
                            updateFhirIdInRelation { errorReceived, errorMsg ->
                                error(errorReceived, errorMsg)
                            }
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            updateFhirIdInPrescription { errorReceived, errorMsg ->
                                error(errorReceived, errorMsg)
                            }
                        }

                        /** Download Patient Worker */
                        downloadPatientWorker { errorReceived, errorMsg ->
                            error(errorReceived, errorMsg)
                        }
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
                    if (workInfo.progress.getInt(
                            RelationUploadSyncWorker.RelationUploadProgress,
                            0
                        ) == 100
                    ) {
                        /** Handle Progress Based Download WorkRequests Here */
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {

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
        Sync.oneTimeSync<PatientPatchUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                    if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsgFromServer
                    )
                } else {
                    if (workInfo.progress.getInt(
                            PatientPatchUploadSyncWorker.PatientPatchUpload,
                            0
                        ) == 100
                    ) {
                        /** Handle Progress Based Download WorkRequests Here */
                    }
                }
            }
        }
    }

    //Relation Patch Sync
    internal suspend fun setRelationPatchWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<RelationPatchUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                if (workInfo.state == WorkInfo.State.FAILED) {
                    val errorMsgFromServer = workInfo.progress.getString(ERROR_MESSAGE) ?: ""
                    if (errorMsgFromServer == ErrorConstants.SESSION_EXPIRED || errorMsgFromServer == ErrorConstants.UNAUTHORIZED) error(
                        true,
                        errorMsgFromServer
                    )
                } else {
                    if (workInfo.progress.getInt(
                            RelationPatchUploadSyncWorker.RelationPatchUpload,
                            0
                        ) == 100
                    ) {
                        /** Handle Progress Based Download WorkRequests Here */
                    }
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
        genericRepository.updateRelationFhirId()
        /** Start Relation Worker */
        uploadRelationWorker { errorReceived, errorMsg ->
            error(errorReceived, errorMsg)
        }
    }

    private suspend fun updateFhirIdInPrescription(error: (Boolean, String) -> Unit) {
        genericRepository.updatePrescriptionFhirId()
        /** Start Prescription Worker */
        uploadPrescriptionSyncWorker { errorReceived, errorMsg ->
            error(errorReceived, errorMsg)
        }
    }
}