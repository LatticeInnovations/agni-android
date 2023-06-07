package com.latticeonfhir.android.service.workmanager.workers.upload.prescription

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository

class PrescriptionUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): PrescriptionUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = syncRepository
}