package com.latticeonfhir.android.service.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters

class AppointmentNoShowStatusUpdateWorkerImpl(
    context: Context,
    workerParameters: WorkerParameters
) : AppointmentNoShowStatusUpdateWorker(context, workerParameters)