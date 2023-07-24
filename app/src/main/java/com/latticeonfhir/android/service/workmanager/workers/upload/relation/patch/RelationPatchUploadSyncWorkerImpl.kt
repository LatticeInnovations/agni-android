package com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class RelationPatchUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): RelationPatchUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepo()
}