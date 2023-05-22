package com.latticeonfhir.android.service.workmanager.workers.download.patient

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import kotlinx.coroutines.delay

abstract class PatientDownloadSyncWorker(context: Context, workerParameters: WorkerParameters) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setProgress(workDataOf(PatientDownloadProgress to 0))
        val response = getSyncRepository().getAndInsertListPatientData(0)
        response.run {
            return when(this) {
                is ApiContinueResponse -> Result.success()
                is ApiEndResponse -> {
                    setProgress(workDataOf(PatientDownloadProgress to 100))
                    Result.success()
                }
                is ApiErrorResponse -> Result.failure(workDataOf("errorMsg" to errorMessage))
                else -> Result.retry()
            }
        }
    }

    companion object {
        const val PatientDownloadProgress = "PatientDownloadProgress"
        private const val delayDuration = 1L
    }
}