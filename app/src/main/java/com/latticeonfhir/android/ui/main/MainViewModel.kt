package com.latticeonfhir.android.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.defaultRetryConfiguration
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.constants.Id.EMPTY_FHIR_ID
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    syncRepository: SyncRepository,
    application: Application,
    private val genericRepository: GenericRepository,
    private val patientRepository: PatientRepository,
    private val preferenceRepository: PreferenceRepository
) : BaseAndroidViewModel(application) {

    init {
        FhirApp.syncRepository = syncRepository

        // Post Sync Worker
        viewModelScope.launch(Dispatchers.IO) {
            uploadPatientWorker()
        }

        // Patch Sync Workers
        viewModelScope.launch(Dispatchers.IO) {
            setPatientPatchWorker()
        }
        viewModelScope.launch(Dispatchers.IO) {
            setRelationPatchWorker()
        }
    }

    /** Patient Upload Post Sync Worker */
    private suspend fun uploadPatientWorker() {
        //Upload Worker
        Sync.periodicSync<PatientUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt(PatientUploadSyncWorker.PatientUploadProgress, 0)
                if (value == 100) {
                    /** Update Fhir Id in Generic Entity */
                    updateFhirIdInRelation()
                }
                downloadPatientWorker()
            }
        }
    }

    /** Patient Download Sync Worker */
    private suspend fun downloadPatientWorker() {
        /** Download Worker */
        Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                downloadRelationWorker()
            }
        }
    }

    /** Upload Relation Post Sync Worker */
    private suspend fun uploadRelationWorker() {
        //Upload Worker
        Sync.oneTimeSync<RelationUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt(RelationUploadSyncWorker.RelationUploadProgress, 0)
                if (value == 100) {
                    /** Handle Progress Based Download WorkRequests Here */
                }
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    downloadPatientWorker()
                }
            }
        }
    }

    /** Relation Download Sync Worker */
    private suspend fun downloadRelationWorker() {
        //Download Worker
        Sync.oneTimeSync<RelationDownloadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        )
    }

    //Patient Patch Sync
    private suspend fun setPatientPatchWorker() {
        //Upload Worker
        Sync.periodicSync<PatientPatchUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt(PatientPatchUploadSyncWorker.PatientPatchUpload, 0)
                if (value == 100) {
                    /** Handle Progress Based Download WorkRequests Here */
                }
                downloadPatientWorker()
            }
        }
    }

    //Relation Patch Sync
    private suspend fun setRelationPatchWorker() {
        //Upload Worker
        Sync.periodicSync<RelationPatchUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).collectLatest { workInfo ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt(RelationPatchUploadSyncWorker.RelationPatchUpload, 0)
                if (value == 100) {
                    /** Handle Progress Based Download WorkRequests Here */
                }
                downloadPatientWorker()
            }
        }
    }

    private suspend fun updateFhirIdInRelation() {
        CoroutineScope(Dispatchers.IO).launch {
            genericRepository.getNonSyncedPostRelations().forEach { genericEntity ->
                val existingMap = genericEntity.payload.fromJson<MutableMap<String, Any>>()
                    .mapToObject(RelatedPersonResponse::class.java)
                if (existingMap != null) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = genericEntity.patientId,
                        entity = existingMap.copy(
                            id = patientRepository.getPatientById(existingMap.id)[0].fhirId
                                ?: EMPTY_FHIR_ID,
                            relationship = existingMap.relationship.map { relationship ->
                                relationship.copy(
                                    relativeId = patientRepository.getPatientById(relationship.relativeId)[0].fhirId
                                        ?: EMPTY_FHIR_ID
                                )
                            }
                        ),
                        typeEnum = GenericTypeEnum.RELATION,
                        replace = true
                    )
                }
            }
            /** Start Relation Worker */
            uploadRelationWorker()
        }
    }
}