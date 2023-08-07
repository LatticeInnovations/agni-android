package com.latticeonfhir.android.service.workmanager.workers.upload.appointment.statusupdate.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.statusupdate.noshow.AppointmentNoShowStatusUpdateWorker

class AppointmentNoShowStatusUpdateWorkerImpl (context: Context, workerParameters: WorkerParameters): AppointmentNoShowStatusUpdateWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}