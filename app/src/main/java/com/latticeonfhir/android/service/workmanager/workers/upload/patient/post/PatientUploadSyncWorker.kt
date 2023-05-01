package com.latticeonfhir.android.service.workmanager.workers.upload.patient.post

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import kotlinx.coroutines.delay

abstract class PatientUploadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return when(getSyncRepository().sendPersonPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            is ApiEmptyResponse -> {
                setProgress(workDataOf(PatientUploadProgress to 100))
                delay(10L)
                Result.success()
            }
        }
    }

    companion object {
        const val PatientUploadProgress = "patentUploadProgress"
    }
}