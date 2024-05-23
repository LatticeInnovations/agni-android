package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker

abstract class TriggerWorkerPeriodic(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        (applicationContext as FhirApp).launchSyncing()
        return Result.success()
    }
}