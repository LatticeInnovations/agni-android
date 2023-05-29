package com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository

class MedicineDosageDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): MedicineDosageDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = syncRepository
}