package com.latticeonfhir.android.service.workmanager.workers.download.patient

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class PatientDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    PatientDownloadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}