package com.latticeonfhir.android.service.workmanager.workers.upload.relation.post

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class RelationUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    RelationUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}