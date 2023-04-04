package com.latticeonfhir.android

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject

@HiltAndroidApp
class FhirApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }

    companion object {
        val gson: Gson by lazy { GsonBuilder().create() }
        lateinit var syncRepository: SyncRepository
    }
}