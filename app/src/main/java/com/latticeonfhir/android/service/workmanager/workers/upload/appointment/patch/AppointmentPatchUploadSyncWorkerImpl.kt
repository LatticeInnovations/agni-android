package com.latticeonfhir.android.service.workmanager.workers.upload.appointment.patch

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class AppointmentPatchUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): AppointmentPatchUploadSyncWorker(context, workerParameters) {

    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}