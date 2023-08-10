package com.latticeonfhir.android.service.workmanager.workers.upload.patient.post

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class PatientUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters) :
    PatientUploadSyncWorker(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}