package com.latticeonfhir.android.service.workmanager.workers.upload.schedule

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class ScheduleUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    ScheduleUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}