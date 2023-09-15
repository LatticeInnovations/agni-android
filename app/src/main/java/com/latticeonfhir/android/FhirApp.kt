package com.latticeonfhir.android

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import com.latticeonfhir.android.service.sync.SyncService
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.converters.gson.DateDeserializer
import com.latticeonfhir.android.utils.converters.gson.DateSerializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Date
import javax.inject.Inject

@HiltAndroidApp
class FhirApp : Application() {

    @Inject
    lateinit var fhirAppDatabase: FhirAppDatabase

    @Inject
    lateinit var preferenceStorage: PreferenceStorage

    @Inject
    lateinit var patientApiService: PatientApiService

    @Inject
    lateinit var prescriptionApiService: PrescriptionApiService

    @Inject
    lateinit var scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService

    private lateinit var syncRepository: SyncRepository
    private lateinit var genericRepository: GenericRepository
    private lateinit var workRequestBuilder: WorkRequestBuilders
    private lateinit var syncService: SyncService
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    private val fhirEngine: FhirEngine by lazy { FhirEngineProvider.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        val preferenceRepository: PreferenceRepository = PreferenceRepositoryImpl(preferenceStorage)

        syncRepository = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            scheduleAndAppointmentApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            preferenceRepository,
            fhirAppDatabase.getRelationDao(),
            fhirAppDatabase.getMedicationDao(),
            fhirAppDatabase.getPrescriptionDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao()
        )

        genericRepository = GenericRepositoryImpl(
            fhirAppDatabase.getGenericDao(),
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao()
        )

        if (!this::workRequestBuilder.isInitialized) {
            workRequestBuilder = WorkRequestBuilders(this)
        }

        if (!this::syncService.isInitialized) {
            syncService = SyncService(this,syncRepository, genericRepository, preferenceRepository)
        }
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = true,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(
                    baseUrl = "http://192.168.0.107:8080/fhir/",
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
                            if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
                        )
                    ) { Timber.d("App-HttpLog $it") },
                ),
            )
        )
    }

    internal fun getSyncRepository(): SyncRepository {
        return syncRepository
    }

    internal fun getWorkRequestBuilder(): WorkRequestBuilders {
        return workRequestBuilder
    }

    internal fun getSyncService(): SyncService {
        return syncService
    }

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
        }
        fun fhirEngine(context: Context) = (context.applicationContext as FhirApp).fhirEngine
    }
}