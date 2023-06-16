package com.latticeonfhir.android.service.workmanager.workers.upload.patient.post

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.constants.ErrorConstants.ERROR_MESSAGE
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse
import kotlinx.coroutines.delay

abstract class PatientUploadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return when(val response = getSyncRepository().sendPersonPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.retry()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) {
                    setProgress(workDataOf(ERROR_MESSAGE to response.errorMessage))
                    delay(5000)
                    Result.failure()
                }
                else Result.retry()
            }
            is ApiEmptyResponse -> {
                setProgress(workDataOf(PatientUploadProgress to 100))
                delay(5000)
                Result.success()
            }
            is ApiNullResponse -> Result.failure()
        }
    }

    companion object {
        const val PatientUploadProgress = "patentUploadProgress"
    }
}