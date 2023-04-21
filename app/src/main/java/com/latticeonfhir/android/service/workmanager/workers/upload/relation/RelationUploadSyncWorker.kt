package com.latticeonfhir.android.service.workmanager.workers.upload.relation

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse

abstract class RelationUploadSyncWorker(context: Context, workerParameters: WorkerParameters) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return when (getSyncRepository().sendRelatedPersonPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            is ApiEmptyResponse -> Result.success()
        }
    }
}