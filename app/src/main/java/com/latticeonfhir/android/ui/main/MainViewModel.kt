package com.latticeonfhir.android.ui.main

import android.app.Application
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.hasKeyWithValueOfType
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.FhirApp.Companion.gson
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.SyncJobStatus
import com.latticeonfhir.android.service.workmanager.defaultRetryConfiguration
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.constants.Id.EMPTY_FHIR_ID
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    syncRepository: SyncRepository,
    application: Application,
    private val genericDao: GenericDao,
    private val genericRepository: GenericRepository,
    private val patientRepository: PatientRepository
) : BaseAndroidViewModel(application) {

    init {
        FhirApp.syncRepository = syncRepository

        // Post Sync Worker
        viewModelScope.launch(Dispatchers.IO) {
            Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
                getApplication<Application>().applicationContext,
                defaultRetryConfiguration.copy(
                    syncConstraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
            )

            WorkManager.getInstance(getApplication<Application>().applicationContext)
                .getWorkInfosForUniqueWorkLiveData(PatientDownloadSyncWorkerImpl::class.java.name)
                .asFlow()
                .flatMapConcat { it.asFlow() }
                .mapNotNull { workInfo ->
                    workInfo.progress.takeIf {
                        it.keyValueMap.isNotEmpty()
                    }?.let {
                            val state = it.getString("StateType")
//                            val stateData = it.getString("State")
//                            gson.fromJson(stateData, Class.forName(state)) as SyncJobStatus
                            it
                        }
                }.collectLatest {
                    Timber.d("Kingdom ${it.keyValueMap}")
                }
//            setPatientWorker()
        }

        // Patch Sync Workers
//        viewModelScope.launch(Dispatchers.IO) {
//            setPatientPatchWorker()
//        }
//        viewModelScope.launch(Dispatchers.IO) {
//            setRelationPatchWorker()
//        }
    }

    //Patient Post Sync Worker
    private suspend fun setPatientWorker() {
        //Upload Worker
        Sync.periodicSync<PatientUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES),
            )
        ).collectLatest {
            if (it == WorkInfo.State.ENQUEUED) {
                /** Update Fhir Id in Generic Entity */
                updateFhirIdInRelation()

                /** Start Relation Worker */
                setRelationWorker()

                /** Download Worker */
                Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
                    getApplication<Application>().applicationContext,
                    defaultRetryConfiguration.copy(
                        syncConstraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true)
                            .build()
                    )
                )
            }
        }
    }

    //Relation Post Sync Worker
    private suspend fun setRelationWorker() {
        //Upload Worker
        Sync.oneTimeSync<RelationUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).collectLatest {
            if (it == WorkInfo.State.SUCCEEDED) {
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
        }
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
                repeat = RepeatInterval(15, TimeUnit.MINUTES),
            )
        ).collectLatest {
            if (it == WorkInfo.State.ENQUEUED) {
                //Download Worker
                Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
                    getApplication<Application>().applicationContext
                )
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
                repeat = RepeatInterval(15, TimeUnit.MINUTES),
            )
        ).collectLatest {
            if (it == WorkInfo.State.ENQUEUED) {
                //Download Worker
                Sync.oneTimeSync<RelationDownloadSyncWorkerImpl>(
                    getApplication<Application>().applicationContext
                )
            }
        }
    }

    private suspend fun updateFhirIdInRelation() {
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
    }
}