package com.latticeonfhir.android.service.workmanager.workers.status.completed

import android.content.Context
import androidx.work.WorkerParameters

class AppointmentCompletedStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentCompletedStatusUpdateWorker(context, workerParameters)