package com.latticeonfhir.android.service.workmanager.workers.upload.appointment.post

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse
import kotlinx.coroutines.delay

abstract class AppointmentUploadSyncWorker(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return when (val response = getSyncRepository().sendAppointmentPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.retry()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) {
                    setProgress(workDataOf(ErrorConstants.ERROR_MESSAGE to response.errorMessage))
                    delay(5000)
                    Result.failure()
                } else Result.retry()
            }

            is ApiEmptyResponse -> {
                setProgress(workDataOf(AppointmentUploadProgress to 100))
                delay(5000)
                Result.success()
            }

            is ApiNullResponse -> Result.failure()
        }
    }

    companion object {
        const val AppointmentUploadProgress = "appointmentUploadProgress"
    }
}