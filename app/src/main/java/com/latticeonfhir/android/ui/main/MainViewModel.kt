package com.latticeonfhir.android.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.SyncJobStatus
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.RelationUploadSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
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
            setWorker()
        }
    }

    private suspend fun setWorker() {
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
        //Upload Worker
        Sync.periodicSync<RelationUploadSyncWorker>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES),
            )
        ).collectLatest {
            if (it == WorkInfo.State.ENQUEUED) {
                //Download Worker
                Sync.oneTimeSync<RelationDownloadSyncWorker>(
                    getApplication<Application>().applicationContext
                )
            }
        }
    }

}