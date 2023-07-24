package com.latticeonfhir.android.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class TriggerWorkerImpl(context: Context, workerParameters: WorkerParameters): TriggerWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepo()
}