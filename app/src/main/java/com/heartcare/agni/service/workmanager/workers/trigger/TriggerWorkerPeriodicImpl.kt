package com.heartcare.agni.service.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.heartcare.agni.FhirApp

class TriggerWorkerPeriodicImpl(context: Context, workerParameters: WorkerParameters) :
    TriggerWorkerPeriodic(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}