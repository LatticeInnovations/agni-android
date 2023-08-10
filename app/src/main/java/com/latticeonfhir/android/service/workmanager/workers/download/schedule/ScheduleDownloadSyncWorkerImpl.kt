package com.latticeonfhir.android.service.workmanager.workers.download.schedule

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class ScheduleDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    ScheduleDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}