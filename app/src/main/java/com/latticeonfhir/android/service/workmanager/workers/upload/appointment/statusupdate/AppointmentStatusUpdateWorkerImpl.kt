package com.latticeonfhir.android.service.workmanager.workers.upload.appointment.statusupdate

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentStatusUpdateWorkerImpl (context: Context, workerParameters: WorkerParameters): AppointmentStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}