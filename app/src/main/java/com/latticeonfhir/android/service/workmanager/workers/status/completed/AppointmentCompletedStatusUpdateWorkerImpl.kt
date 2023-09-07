package com.latticeonfhir.android.service.workmanager.workers.status.completed

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentCompletedStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentCompletedStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}