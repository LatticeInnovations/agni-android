package com.latticeonfhir.android.ui.fhirsdk

import android.content.Context
import androidx.work.WorkerParameters
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.latticeonfhir.android.FhirApp

class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getDownloadWorkManager(): DownloadWorkManager {
        return DownloadWorkManagerImpl(FhirApp.preferenceStorage(applicationContext))
    }

    override fun getConflictResolver() = AcceptLocalConflictResolver

    override fun getFhirEngine() = FhirApp.fhirEngine(applicationContext)
}