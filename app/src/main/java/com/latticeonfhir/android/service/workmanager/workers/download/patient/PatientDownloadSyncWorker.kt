package com.latticeonfhir.android.service.workmanager.workers.download.patient

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse

abstract class PatientDownloadSyncWorker(context: Context, workerParameters: WorkerParameters) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(Progress to 0))
        return when(getSyncRepository().getAndInsertListPatientData(0)) {
            is ApiContinueResponse -> {
                setProgress(workDataOf(Progress to 100))
                Result.success()
            }
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            else -> Result.retry()
        }
    }

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }
}