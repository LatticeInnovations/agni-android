package com.latticeonfhir.android.service.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentNoShowStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentNoShowStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).syncRepository
}