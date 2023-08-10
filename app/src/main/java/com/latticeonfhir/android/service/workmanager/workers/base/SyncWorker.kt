package com.latticeonfhir.android.service.workmanager.workers.base

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository

abstract class SyncWorker(context: Context, workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters) {

    abstract fun getSyncRepository(): SyncRepository
}