package com.latticeonfhir.android.service.workmanager.workers.upload.relation.post

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import kotlinx.coroutines.delay

abstract class RelationUploadSyncWorker(context: Context, workerParameters: WorkerParameters) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(RelationUploadProgress to 0))
        return when (getSyncRepository().sendRelatedPersonPostData()) {
            is ApiContinueResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            is ApiEmptyResponse -> {
                setProgress(workDataOf(RelationUploadProgress to 100))
                delay(1000L)
                Result.success()
            }
        }
    }

    companion object {
        const val RelationUploadProgress = "RelationUploadProgress"
    }
}