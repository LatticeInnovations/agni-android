package com.latticeonfhir.android.ui.main

import android.app.Application
import androidx.work.Constraints
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.PatientUploadSyncWorkerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    syncRepository: SyncRepository,
    application: Application
) : BaseAndroidViewModel(application) {

    init {
        FhirApp.syncRepository = syncRepository
        setWorker()
    }

    private fun setWorker() {
        //Upload Worker
        Sync.periodicSync<PatientUploadSyncWorkerImpl>(
            getApplication<Application>().applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
                repeat = RepeatInterval(45, TimeUnit.SECONDS),
            )
        )

        //Download Worker
        Sync.periodicSync<PatientDownloadSyncWorkerImpl>(
            getApplication<Application>().applicationContext, PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build(),
                repeat = RepeatInterval(45, TimeUnit.SECONDS)
            )
        )
    }

}