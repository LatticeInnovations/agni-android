package com.latticeonfhir.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.PeriodicSyncJobStatus
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.service.fhirsync.FhirPeriodicSyncWorker
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.converters.gson.DateDeserializer
import com.latticeonfhir.android.utils.converters.gson.DateSerializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FhirApp : Application() {

    private val _fhirEngine: FhirEngine by lazy { FhirEngineProvider.getInstance(this) }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var preferenceStorage: PreferenceStorage
    private lateinit var _workRequestBuilder: WorkRequestBuilders
    internal val workRequestBuilder get() = _workRequestBuilder
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    internal var periodicSyncJobStatus = MutableLiveData<PeriodicSyncJobStatus>()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
        if (!this::_workRequestBuilder.isInitialized) {
            _workRequestBuilder = WorkRequestBuilders(this)
        }
        initializeFhirEngine()
        if (preferenceStorage.token.isNotBlank()) {
            enqueueWorker()
        }
    }

    private fun initializeFhirEngine() {
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = !BuildConfig.DEBUG,
                databaseErrorStrategy = DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(
                    baseUrl = BuildConfig.FHIR_URL,
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
                            if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.NONE
                        )
                    ) {
                        Timber.d("App-HttpLog $it")
                    },
                    //authenticator = { HttpAuthenticationMethod.Bearer(preferenceStorage.token) }
                ),
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun enqueueWorker() = CoroutineScope(Dispatchers.IO).launch {
        Sync.periodicSync<FhirPeriodicSyncWorker>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
                repeat = RepeatInterval(15, TimeUnit.MINUTES)
            )
        ).shareIn(this, SharingStarted.Eagerly, 10)
            .collect { syncJobStatus ->
                periodicSyncJobStatus.postValue(syncJobStatus)
                Timber.d("sync done $syncJobStatus")
            }
    }

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
        }

        fun fhirEngine(context: Context) = (context.applicationContext as FhirApp)._fhirEngine

        fun sharedPreferences(context: Context) =
            (context.applicationContext as FhirApp).sharedPreferences

        fun runEnqueuedWorker(context: Context) {
            (context.applicationContext as FhirApp).enqueueWorker()
        }
    }
}