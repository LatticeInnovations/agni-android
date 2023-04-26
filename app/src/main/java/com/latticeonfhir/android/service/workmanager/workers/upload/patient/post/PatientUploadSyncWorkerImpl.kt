package com.latticeonfhir.android.service.workmanager.workers.upload.patient.post

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp.Companion.syncRepository
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorker

class PatientUploadSyncWorkerImpl(context: Context, workerParameters: WorkerParameters): PatientUploadSyncWorker(context,workerParameters) {
    override fun getSyncRepository() = syncRepository
}