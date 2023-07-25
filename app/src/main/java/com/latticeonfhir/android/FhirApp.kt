package com.latticeonfhir.android

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.converters.gson.DateDeserializer
import com.latticeonfhir.android.utils.converters.gson.DateSerializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Date
import javax.inject.Inject

@HiltAndroidApp
class FhirApp : Application() {

    @Inject lateinit var fhirAppDatabase: FhirAppDatabase
    @Inject lateinit var preferenceStorage: PreferenceStorage
    @Inject lateinit var patientApiService: PatientApiService
    @Inject lateinit var prescriptionApiService: PrescriptionApiService

    private lateinit var syncRepository: SyncRepository
    private lateinit var workRequestBuilder: WorkRequestBuilders
    val sessionExpireFlow = MutableStateFlow(emptyMap<String,Any>())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        syncRepository = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            PreferenceRepositoryImpl(preferenceStorage) as PreferenceRepository,
            fhirAppDatabase.getRelationDao(),
            fhirAppDatabase.getMedicationDao(),
            fhirAppDatabase.getPrescriptionDao()
        )

        if(!this::workRequestBuilder.isInitialized) {
            workRequestBuilder = WorkRequestBuilders(
                this,
                GenericRepositoryImpl(
                    this,
                    fhirAppDatabase.getGenericDao(),
                    fhirAppDatabase.getPatientDao()
                )
            )
        }
    }

    internal fun getSyncRepository(): SyncRepository {
        return syncRepository
    }

    internal fun geWorkRequestBuilder(): WorkRequestBuilders {
        return workRequestBuilder
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