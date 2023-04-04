package com.latticeonfhir.android.service.workmanager.workers.upload.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository

class RelationUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): RelationUploadSyncWorker(context, workerParameters) {

    override fun getSyncRepository() = syncRepository

}