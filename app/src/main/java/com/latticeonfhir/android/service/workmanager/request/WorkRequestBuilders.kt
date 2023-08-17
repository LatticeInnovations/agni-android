package com.latticeonfhir.android.service.workmanager.request

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.service.workmanager.utils.Delay
import com.latticeonfhir.android.service.workmanager.utils.InitialDelay
import com.latticeonfhir.android.service.workmanager.utils.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.utils.RepeatInterval
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.utils.defaultRetryConfiguration
import com.latticeonfhir.android.service.workmanager.workers.download.appointment.AppointmentDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.medication.MedicationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage.MedicineDosageDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.prescription.PrescriptionDownloadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.download.prescription.PrescriptionDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.schedule.ScheduleDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.trigger.triggeronetime.TriggerWorkerOneTimeImpl
import com.latticeonfhir.android.service.workmanager.workers.trigger.triggerperiodic.TriggerWorkerPeriodicImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.patch.AppointmentPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.post.AppointmentUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.statusupdate.completed.AppointmentCompletedStatusUpdateWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.statusupdate.noshow.AppointmentNoShowStatusUpdateWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.prescription.PrescriptionUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.schedule.ScheduleUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.constants.ErrorConstants.ERROR_MESSAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class WorkRequestBuilders(
    private val applicationContext: Context,
    private val genericRepository: GenericRepository
) {

    /** Download Sync Variables */
    private var patientSyncCompleted = false
    private var scheduleSyncCompleted = false

    /** Upload / Patch Sync Variables */

    /**
     *
     * Periodic Worker that triggers
     * every other worker when app is in foreground or not
     *
     */
    internal suspend fun setPeriodicTriggerWorker() {
        Sync.periodicSync<TriggerWorkerPeriodicImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collect()
    }

    /**
     *
     * One Time Worker that triggers
     * every other worker when app is in foreground or not
     *
     */
    internal suspend fun setOneTimeTriggerWorker() {
        Sync.oneTimeSync<TriggerWorkerOneTimeImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collect()
    }

    /**
     *
     * Periodic Worker that triggers
     * method to update status to "No-Show" at 11:59 PM everyday
     *
     */
    internal suspend fun setPeriodicAppointmentNoShowStatusUpdateWorker(
        duration: Duration?,
        delay: Delay?
    ) {
        Sync.periodicSync<AppointmentNoShowStatusUpdateWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(24, TimeUnit.HOURS),
                initialDelay = InitialDelay(duration, delay)
            )
        ).collect()
    }

    /**
     *
     * Periodic Worker that triggers
     * method to update status to "Completed" at 11:59 PM everyday
     *
     */
    internal suspend fun setPeriodicAppointmentCompletedStatusUpdateWorker(
        duration: Duration?,
        delay: Delay?
    ) {
        Sync.periodicSync<AppointmentCompletedStatusUpdateWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(24, TimeUnit.HOURS),
                initialDelay = InitialDelay(duration, delay)
            )
        ).collect()
    }


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
        //Upload Worker
        Sync.oneTimeSync<PatientUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
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
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    /** Update Fhir Id in Generic Entity */
                    CoroutineScope(Dispatchers.IO).launch {
                        updateFhirIdInRelation { errorReceived, errorMsg ->
                            error(errorReceived, errorMsg)
                        }
                    }
                    /** Upload Schedule Worker */
                    CoroutineScope(Dispatchers.IO).launch {
                        uploadScheduleSyncWorker { errorReceived, errorMsg ->
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

    /** Upload Relation Post Sync Worker */
    private suspend fun uploadRelationWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<RelationUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            }
        }
    }

    /** Upload Prescription Data */
    private suspend fun uploadPrescriptionSyncWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<PrescriptionUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            }
        }
    }

    /** Upload Schedule Data */
    private suspend fun uploadScheduleSyncWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<ScheduleUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
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

                    /** Update Schedule Fhir Id in Appointment Patch Worker*/
                    CoroutineScope(Dispatchers.IO).launch {
                        updateScheduleFhirIdInAppointmentPatch { errorReceived, errorMsg ->
                            error(errorReceived, errorMsg)
                        }
                    }

                    /**call update fhir id in appointment worker */
                    updateFhirIdsInAppointment { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
                    }
                }
            }
        }
    }

    /** Upload Appointment Data */
    private suspend fun uploadAppointmentSyncWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<AppointmentUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
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
                    updateFhirIdInPrescription { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
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
                patientSyncCompleted = true
                CoroutineScope(Dispatchers.IO).launch {
                    downloadRelationWorker { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
                    }
                }
                downloadAppointmentWorker { errorReceived, errorMsg ->
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
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
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
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            }
        }
    }

    /** Schedule Download Sync Worker */
    private suspend fun downloadScheduleWorker(error: (Boolean, String) -> Unit) {
        /** Download Worker */
        Sync.oneTimeSync<ScheduleDownloadSyncWorkerImpl>(
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
                scheduleSyncCompleted = true
                downloadAppointmentWorker { errorReceived, errorMsg ->
                    error(errorReceived, errorMsg)
                }
            }
        }
    }

    /** Appointment Download Sync Worker */
    private suspend fun downloadAppointmentWorker(error: (Boolean, String) -> Unit) {
        /** Download Worker */
        if (patientSyncCompleted && scheduleSyncCompleted) {
            Sync.oneTimeSync<AppointmentDownloadSyncWorkerImpl>(
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
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
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
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
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
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            }
        }
    }

    //Relation Patch Sync
    internal suspend fun setRelationPatchWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<RelationPatchUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.FAILED) {
                val errorMsg = workInfo.outputData.keyValueMap["errorMsg"].toString()
                if (errorMsg == ErrorConstants.SESSION_EXPIRED || errorMsg == ErrorConstants.UNAUTHORIZED) error(
                    true,
                    errorMsg
                )
            }
        }
    }

    //Appointment Patch Sync
    private suspend fun setAppointmentPatchWorker(error: (Boolean, String) -> Unit) {
        //Upload Worker
        Sync.oneTimeSync<AppointmentPatchUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
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
                } else if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    downloadScheduleWorker { errorReceived, errorMsg ->
                        error(errorReceived, errorMsg)
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

    private suspend fun updateFhirIdsInAppointment(error: (Boolean, String) -> Unit) {
        genericRepository.updateAppointmentFhirIds()
        /** Start Appointment Worker */
        uploadAppointmentSyncWorker { errorReceived, errorMsg ->
            error(errorReceived, errorMsg)
        }
    }

    private suspend fun updateScheduleFhirIdInAppointmentPatch(error: (Boolean, String) -> Unit) {
        genericRepository.updateAppointmentFhirIdInPatch()
        /** Start Appointment Patch Worker */
        setAppointmentPatchWorker { errorReceived, errorMsg ->
            error(errorReceived, errorMsg)
        }
    }
}