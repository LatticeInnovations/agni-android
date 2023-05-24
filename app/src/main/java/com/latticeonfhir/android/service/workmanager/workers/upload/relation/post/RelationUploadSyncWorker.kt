package com.latticeonfhir.android.service.workmanager.workers.upload.relation.post

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

abstract class RelationUploadSyncWorker(context: Context, workerParameters: WorkerParameters) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(RelationUploadProgress to 0))
        return when (val response = getSyncRepository().sendRelatedPersonPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) Result.failure(
                    workDataOf("errorMsg" to response.errorMessage)
                )
                else Result.retry()
            }
            is ApiEmptyResponse -> {
                setProgress(workDataOf(RelationUploadProgress to 100))
                delay(1000L)
                Result.success()
            }
            is ApiNullResponse -> Result.failure()
        }
    }

    companion object {
        const val RelationUploadProgress = "RelationUploadProgress"
    }
}