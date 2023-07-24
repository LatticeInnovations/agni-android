package com.latticeonfhir.android.service.workmanager.workers.download.medication

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class MedicationDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): MedicationDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepo()
}