package com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse
import kotlinx.coroutines.delay

abstract class RelationPatchUploadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(RelationPatchUpload to 0))
        return when (getSyncRepository().sendRelatedPersonPatchData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.retry()
            is ApiEmptyResponse -> {
                setProgress(workDataOf(RelationPatchUpload to 100))
                delay(1L)
                Result.success()
            }
            is ApiNullResponse -> Result.failure()
        }
    }

    companion object {
        const val RelationPatchUpload = "RelationPatchUpload"
    }
}