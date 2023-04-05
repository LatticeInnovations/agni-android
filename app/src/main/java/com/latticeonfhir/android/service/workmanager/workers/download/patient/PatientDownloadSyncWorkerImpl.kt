package com.latticeonfhir.android.service.workmanager.workers.download.patient

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository

class PatientDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): PatientDownloadSyncWorker(context,workerParameters) {

    override fun getSyncRepository() = syncRepository
}