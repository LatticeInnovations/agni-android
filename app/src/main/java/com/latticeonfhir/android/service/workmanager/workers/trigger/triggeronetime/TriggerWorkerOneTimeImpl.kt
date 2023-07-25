package com.latticeonfhir.android.service.workmanager.workers.trigger.triggeronetime

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp

class TriggerWorkerOneTimeImpl(context: Context, workerParameters: WorkerParameters): TriggerWorkerOneTime(context, workerParameters) {
    override fun getSyncRepository() = (applicationContext as FhirApp).getSyncRepository()
}