package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TriggerWorkerPeriodic(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        (applicationContext as FhirApp).getSyncService()
            .syncLauncher { errorReceived, errorMessage ->
                CoroutineScope(Dispatchers.Main).launch {
                    (applicationContext as FhirApp).sessionExpireFlow.postValue(
                        mapOf(Pair("errorReceived", errorReceived), Pair("errorMsg", errorMessage))
                    )
                }
            }
        return Result.success()
    }
}