package com.latticeonfhir.android.service.workmanager.workers.base

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

abstract class SyncWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters)