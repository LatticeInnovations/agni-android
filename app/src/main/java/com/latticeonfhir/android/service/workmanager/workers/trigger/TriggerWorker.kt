package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker

abstract class TriggerWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}