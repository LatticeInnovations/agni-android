package com.latticeonfhir.android.service.workmanager.workers.download.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker

abstract class RelationDownloadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}