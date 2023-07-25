package com.latticeonfhir.android.service.workmanager.workers.download.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class RelationDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): RelationDownloadSyncWorker(context,workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}