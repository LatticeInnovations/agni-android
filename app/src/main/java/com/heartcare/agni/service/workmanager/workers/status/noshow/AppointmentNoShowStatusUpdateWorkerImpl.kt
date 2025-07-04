package com.heartcare.agni.service.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.heartcare.agni.FhirApp

class AppointmentNoShowStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentNoShowStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}