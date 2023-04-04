package com.latticeonfhir.android.service.workmanager.workers.upload.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker

abstract class RelationUploadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return Result.failure()
    }
}