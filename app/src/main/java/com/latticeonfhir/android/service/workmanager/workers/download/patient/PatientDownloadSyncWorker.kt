package com.latticeonfhir.android.service.workmanager.workers.download.patient

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse

abstract class PatientDownloadSyncWorker(context: Context, workerParameters: WorkerParameters): SyncWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return when(getSyncRepository().getAndInsertListPatientData()) {
            is ApiSuccessResponse -> Result.success()
            is ApiEndResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            else -> Result.retry()
        }
    }
}