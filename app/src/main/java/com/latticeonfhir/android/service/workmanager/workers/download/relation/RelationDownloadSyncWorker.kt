package com.latticeonfhir.android.service.workmanager.workers.download.relation

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse

abstract class RelationDownloadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return when(val response = getSyncRepository().getAndInsertRelation()) {
            is ApiEmptyResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) Result.failure(
                    workDataOf("errorMsg" to response.errorMessage)
                )
                else Result.retry()
            }
            is ApiNullResponse -> Result.failure()
            else -> Result.failure()
        }
    }
}