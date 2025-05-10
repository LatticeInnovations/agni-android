package com.latticeonfhir.sync.workmanager.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters

class AppointmentNoShowStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentNoShowStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}