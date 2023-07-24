package com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class PatientPatchUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): PatientPatchUploadSyncWorker(context, workerParameters) {

    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepo()
}