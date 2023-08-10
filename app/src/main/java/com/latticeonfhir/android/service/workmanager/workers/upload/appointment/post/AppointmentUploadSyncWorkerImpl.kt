package com.latticeonfhir.android.service.workmanager.workers.upload.appointment.post

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    AppointmentUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}