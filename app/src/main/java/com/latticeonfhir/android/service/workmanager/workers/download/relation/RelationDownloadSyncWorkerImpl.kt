package com.latticeonfhir.android.service.workmanager.workers.download.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository

class RelationDownloadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): RelationDownloadSyncWorker(context,workerParameters) {

    override fun getSyncRepository() = syncRepository
}