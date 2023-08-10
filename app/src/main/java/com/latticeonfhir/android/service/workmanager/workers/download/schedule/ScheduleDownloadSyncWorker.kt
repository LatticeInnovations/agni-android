package com.latticeonfhir.android.service.workmanager.workers.download.schedule

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse

abstract class ScheduleDownloadSyncWorker(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(ScheduleDownloadProgress to 0))

        return when (val response = getSyncRepository().getAndInsertSchedule(0)) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> {
                setProgress(workDataOf(ScheduleDownloadProgress to 100))
                Result.success()
            }

            is ApiEmptyResponse -> Result.failure()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) Result.failure(
                    workDataOf("errorMsg" to response.errorMessage)
                )
                else Result.retry()
            }

            else -> Result.retry()
        }
    }

    companion object {
        const val ScheduleDownloadProgress = "ScheduleDownloadProgress"
    }
}