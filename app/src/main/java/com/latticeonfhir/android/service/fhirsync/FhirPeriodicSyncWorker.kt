package com.latticeonfhir.android.service.fhirsync

import android.content.Context
import androidx.work.WorkerParameters
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.upload.UploadStrategy
import com.latticeonfhir.android.FhirApp

class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getDownloadWorkManager(): DownloadWorkManager {
        return DownloadWorkManagerImpl(FhirApp.sharedPreferences(applicationContext))
    }

    override fun getConflictResolver() = AcceptLocalConflictResolver

    override fun getFhirEngine() = FhirApp.fhirEngine(applicationContext)

    override fun getUploadStrategy(): UploadStrategy {
        return UploadStrategy.AllChangesSquashedBundlePut
    }
}