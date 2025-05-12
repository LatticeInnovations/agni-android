package com.latticeonfhir.sync.workmanager.workmanager.workers.base

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository

abstract class SyncWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    abstract fun getSyncRepository(): SyncRepository
}