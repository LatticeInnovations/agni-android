package com.latticeonfhir.android.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    syncRepository: SyncRepository,
    application: Application
) : BaseAndroidViewModel(application) {

    init {
        FhirApp.syncRepository = syncRepository
        viewModelScope.launch(Dispatchers.IO) {
            setPatientWorker()
        }
        viewModelScope.launch(Dispatchers.IO) {
            setRelationWorker()
        }
        viewModelScope.launch(Dispatchers.IO) {
            setPatientPatchWorker()
        }
        viewModelScope.launch(Dispatchers.IO) {
            setRelationPatchWorker()
        }
    }

    //Patient Post Sync Worker
    private suspend fun setPatientWorker() {
        //Upload Worker
        Sync.periodicSync<PatientUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
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

    //Relation Post Sync Worker
    private suspend fun setRelationWorker() {
        //Upload Worker
        Sync.periodicSync<RelationUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
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

    //Patient Patch Sync
    private suspend fun setPatientPatchWorker() {
        //Upload Worker
        Sync.periodicSync<PatientPatchUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
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
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
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
}