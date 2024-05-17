package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TriggerWorkerPeriodic(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val listOfErrors = mutableListOf<String>()
        (applicationContext as FhirApp).syncWorkerStatus.postValue(WorkerStatus.IN_PROGRESS)
        (applicationContext as FhirApp).syncService
            .syncLauncher { errorReceived, errorMessage ->
                // as there will be multiple callbacks from different coroutines
                // list of errors is maintained.
                // if the list is empty, then all the api calls were successful.
                listOfErrors.add(errorMessage)
                (applicationContext as FhirApp).syncWorkerStatus.postValue(WorkerStatus.FAILED)
                CoroutineScope(Dispatchers.Main).launch {
                    (applicationContext as FhirApp).sessionExpireFlow.postValue(
                        mapOf(Pair("errorReceived", errorReceived), Pair("errorMsg", errorMessage))
                    )
                }
            }.also {
                if(listOfErrors.isEmpty()) (applicationContext as FhirApp).syncWorkerStatus.postValue(WorkerStatus.SUCCESS)
                else (applicationContext as FhirApp).syncWorkerStatus.postValue(WorkerStatus.FAILED)
            }
        return Result.success()
    }
}