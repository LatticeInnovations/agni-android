package com.latticeonfhir.core.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class TriggerWorkerPeriodicImpl(context: Context, workerParameters: WorkerParameters) :
    TriggerWorkerPeriodic(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}