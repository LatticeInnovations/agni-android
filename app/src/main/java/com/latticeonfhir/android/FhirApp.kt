package com.latticeonfhir.android

import android.app.Application
import androidx.lifecycle.MutableLiveData
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

    private lateinit var _syncRepository: SyncRepository
    internal val syncRepository get() = _syncRepository
    private lateinit var _genericRepository: GenericRepository
    internal val genericRepository get() = _genericRepository
    private lateinit var _workRequestBuilder: WorkRequestBuilders
    internal val workRequestBuilder get() = _workRequestBuilder
    private lateinit var _syncService: SyncService
    internal val syncService get() = _syncService
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        val preferenceRepository: PreferenceRepository = PreferenceRepositoryImpl(preferenceStorage)

        _syncRepository = SyncRepositoryImpl(
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

        _genericRepository = GenericRepositoryImpl(
            fhirAppDatabase.getGenericDao(),
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao()
        )

        if (!this::_workRequestBuilder.isInitialized) {
            _workRequestBuilder = WorkRequestBuilders(this)
        }

        if (!this::_syncService.isInitialized) {
            _syncService = SyncService(this,syncRepository, genericRepository, preferenceRepository)
        }
    }

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
        }
    }
}