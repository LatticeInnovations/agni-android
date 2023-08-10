package com.latticeonfhir.android.service.workmanager.workers.upload.prescription

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse
import kotlinx.coroutines.delay

abstract class PrescriptionUploadSyncWorker(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return when (val response = getSyncRepository().sendPrescriptionPostData()) {
            is ApiEndResponse -> Result.retry()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) Result.failure(
                    workDataOf("errorMsg" to response.errorMessage)
                )
                else Result.retry()
            }

            is ApiEmptyResponse -> {
                setProgress(workDataOf(PRESCRIPTION_UPLOAD_PROGRESS to 100))
                delay(10L)
                Result.success()
            }

            is ApiNullResponse -> Result.failure()
            else -> Result.retry()
        }
    }

    companion object {
        const val PRESCRIPTION_UPLOAD_PROGRESS = "PrescriptionUploadProgress"
    }
}