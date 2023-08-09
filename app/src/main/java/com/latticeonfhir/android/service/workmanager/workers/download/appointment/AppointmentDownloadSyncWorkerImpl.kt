package com.latticeonfhir.android.service.workmanager.workers.download.appointment

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentDownloadSyncWorkerImpl (context: Context, workerParameters: WorkerParameters): AppointmentDownloadSyncWorker(context,workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}