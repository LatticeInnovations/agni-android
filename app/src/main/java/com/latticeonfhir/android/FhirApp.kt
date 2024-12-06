package com.latticeonfhir.android

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.CVDApiService
import com.latticeonfhir.android.data.server.api.DispenseApiService
import com.latticeonfhir.android.data.server.api.FileUploadApiService
import com.latticeonfhir.android.data.server.api.LabTestAndMedRecordService
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.api.VitalApiService
import com.latticeonfhir.android.data.server.api.SymptomsAndDiagnosisService
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepositoryImpl
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import com.latticeonfhir.android.service.sync.SyncService
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.converters.gson.DateDeserializer
import com.latticeonfhir.android.utils.converters.gson.DateSerializer
import com.latticeonfhir.android.utils.network.CheckNetwork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
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

    @Inject
    lateinit var cvdApiService: CVDApiService
    @Inject
    lateinit var vitalApiService: VitalApiService
    @Inject
    lateinit var symptomsAndDiagnosisService: SymptomsAndDiagnosisService
    @Inject
    lateinit var labTestAndMedRecordService: LabTestAndMedRecordService
    @Inject
    lateinit var dispenseApiService: DispenseApiService

    @Inject
    lateinit var fileUploadApiService: FileUploadApiService

    private lateinit var _syncRepository: SyncRepository
    private val fileSyncRepository get() = _fileSyncRepository
    private lateinit var _fileSyncRepository: FileSyncRepository
    internal val syncRepository get() = _syncRepository
    private lateinit var _genericRepository: GenericRepository
    internal val genericRepository get() = _genericRepository
    private lateinit var _workRequestBuilder: WorkRequestBuilders
    internal val workRequestBuilder get() = _workRequestBuilder
    private lateinit var _syncService: SyncService
    internal val syncService get() = _syncService
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    private lateinit var _symDiagRepository: SymptomsAndDiagnosisRepository
    private val symDiagRepository get() = _symDiagRepository
    internal var syncWorkerStatus = MutableLiveData<WorkerStatus>()
    internal var photosWorkerStatus = MutableLiveData<WorkerStatus>()
    private val isSyncing = AtomicBoolean(false)

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
            cvdApiService,
            vitalApiService,
            symptomsAndDiagnosisService,
            labTestAndMedRecordService,
            dispenseApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            preferenceRepository,
            fhirAppDatabase.getRelationDao(),
            fhirAppDatabase.getMedicationDao(),
            fhirAppDatabase.getPrescriptionDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao(),
            fhirAppDatabase.getPatientLastUpdatedDao(),
            fhirAppDatabase.getCVDDao(),
            fhirAppDatabase.getVitalDao(),
            fhirAppDatabase.getSymptomsAndDiagnosisDao(),
            fhirAppDatabase.getLabTestAndMedDao(),
            fhirAppDatabase.getDispenseDao()
        )

        _fileSyncRepository = FileSyncRepositoryImpl(
            applicationContext,
            fileUploadApiService,
            fhirAppDatabase.getFileUploadDao(),
            fhirAppDatabase.getDownloadedFileDao(),
            fhirAppDatabase.getGenericDao()
        )

        _genericRepository = GenericRepositoryImpl(
            fhirAppDatabase.getGenericDao(),
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao(),
            fhirAppDatabase.getPrescriptionDao()
        )

        _symDiagRepository = SymptomsAndDiagnosisRepositoryImpl(
            symptomsAndDiagnosisService,
            fhirAppDatabase.getSymptomsAndDiagnosisDao()
        )
        if (!this::_workRequestBuilder.isInitialized) {
            _workRequestBuilder = WorkRequestBuilders(this)
        }

        if (!this::_syncService.isInitialized) {
            _syncService =
                SyncService(
                    this,
                    syncRepository,
                    genericRepository,
                    preferenceRepository,
                    fileSyncRepository,
                    symptomsAndDiagnosisRepository = symDiagRepository
                )
        }
    }

    internal suspend fun launchSyncing() {
        if (isSyncing.compareAndSet(false, true)) {
            try {
                if (CheckNetwork.isInternetAvailable(applicationContext)) {
                    val listOfErrors = mutableListOf<String>()
                    syncWorkerStatus.postValue(WorkerStatus.IN_PROGRESS)
                    preferenceStorage.syncStatus = SyncStatusMessageEnum.SYNCING_IN_PROGRESS.display
                    syncService.syncLauncher { errorReceived, errorMessage ->
                        // as there will be multiple callbacks from different coroutines
                        // list of errors is maintained.
                        // if the list is empty, then all the api calls were successful.
                        listOfErrors.add(errorMessage)
                        CoroutineScope(Dispatchers.Main).launch {
                            (applicationContext as FhirApp).sessionExpireFlow.postValue(
                                mapOf(
                                    Pair("errorReceived", errorReceived),
                                    Pair("errorMsg", errorMessage)
                                )
                            )
                        }
                    }.also {
                        withContext(Dispatchers.Main) {
                            photosWorkerStatus.observeForever { photosSyncStatus ->
                                when (photosSyncStatus) {
                                    WorkerStatus.SUCCESS -> {
                                        preferenceStorage.lastSyncTime = Date().time
                                        if (listOfErrors.isEmpty()) {
                                            preferenceStorage.syncStatus =
                                                SyncStatusMessageEnum.SYNCING_COMPLETED.display
                                            syncWorkerStatus.postValue(WorkerStatus.SUCCESS)
                                        } else {
                                            preferenceStorage.syncStatus =
                                                SyncStatusMessageEnum.SYNCING_FAILED.display
                                            syncWorkerStatus.postValue(WorkerStatus.FAILED)
                                        }
                                    }

                                    WorkerStatus.FAILED -> {
                                        preferenceStorage.lastSyncTime = Date().time
                                        preferenceStorage.syncStatus =
                                            SyncStatusMessageEnum.SYNCING_FAILED.display
                                        syncWorkerStatus.postValue(WorkerStatus.FAILED)
                                    }

                                    else -> {
                                        Timber.d("manseeyy photos sync status $photosWorkerStatus")
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                isSyncing.set(false)
            }
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