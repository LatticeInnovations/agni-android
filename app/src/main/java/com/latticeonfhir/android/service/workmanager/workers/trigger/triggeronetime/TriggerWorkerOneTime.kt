package com.latticeonfhir.android.service.workmanager.workers.trigger.triggeronetime

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TriggerWorkerOneTime(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        with((applicationContext as FhirApp).getWorkRequestBuilder()) {
            CoroutineScope(Dispatchers.IO).launch {
                uploadPatientWorker { errorReceived, errorMsg ->
                    CoroutineScope(Dispatchers.IO).launch {
                        (applicationContext as FhirApp).sessionExpireFlow.emit(
                            mapOf(Pair("errorReceived",errorReceived),Pair("errorMsg",errorMsg))
                        )
                    }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                setPatientPatchWorker { errorReceived, errorMsg ->
                    CoroutineScope(Dispatchers.IO).launch {
                        (applicationContext as FhirApp).sessionExpireFlow.emit(
                            mapOf(Pair("errorReceived",errorReceived),Pair("errorMsg",errorMsg))
                        )
                    }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                setRelationPatchWorker { errorReceived, errorMsg ->
                    CoroutineScope(Dispatchers.IO).launch {
                        (applicationContext as FhirApp).sessionExpireFlow.emit(
                            mapOf(Pair("errorReceived",errorReceived),Pair("errorMsg",errorMsg))
                        )
                    }
                }
            }
        }
        return Result.success()
    }
}