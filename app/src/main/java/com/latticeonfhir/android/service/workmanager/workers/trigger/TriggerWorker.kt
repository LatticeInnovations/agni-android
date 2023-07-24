package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TriggerWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        (applicationContext as FhirApp).geWorkRequestBuilder().apply {
            CoroutineScope(Dispatchers.IO).launch {
                uploadPatientWorker { _, _ ->  }
                setPatientPatchWorker { _, _ ->  }
                setRelationPatchWorker { _, _ -> }
            }
        }
        return Result.success()
    }
}