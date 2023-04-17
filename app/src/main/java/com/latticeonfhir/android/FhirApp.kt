package com.latticeonfhir.android

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.BackoffCriteria
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.RetryConfiguration
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.converters.gson.DateDeserializer
import com.latticeonfhir.android.utils.converters.gson.DateSerializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.*
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FhirApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }


    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .create()
        }

        lateinit var syncRepository: SyncRepository
    }
}