package com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.constants.ErrorConstants
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse

abstract class MedicineDosageDownloadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return when(val response = getSyncRepository().getMedicineTime()) {
            is ApiEndResponse -> Result.success()
            is ApiContinueResponse -> Result.success()
            is ApiErrorResponse -> {
                if (response.errorMessage == ErrorConstants.SESSION_EXPIRED || response.errorMessage == ErrorConstants.UNAUTHORIZED) Result.failure(
                    workDataOf("errorMsg" to response.errorMessage)
                )
                else Result.retry()
            }
            else -> Result.retry()
        }
    }
}