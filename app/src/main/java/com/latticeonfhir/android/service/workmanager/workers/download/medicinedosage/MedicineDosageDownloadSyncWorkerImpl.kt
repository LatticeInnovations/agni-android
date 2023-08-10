package com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class MedicineDosageDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    MedicineDosageDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}