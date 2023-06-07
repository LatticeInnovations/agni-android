package com.latticeonfhir.android.service.workmanager.workers.download.prescription

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository

class PrescriptionDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): PrescriptionDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = syncRepository
}