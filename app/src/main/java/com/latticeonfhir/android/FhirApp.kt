package com.latticeonfhir.android

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.latticeonfhir.core.data.repository.server.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.latticeonfhir.android.utils.network.CheckNetwork
import com.latticeonfhir.core.BuildConfig
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepositoryImpl
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepositoryImpl
import com.latticeonfhir.core.data.repository.server.file.FileSyncRepositoryImpl
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.data.repository.server.sync.SyncRepositoryImpl
import com.latticeonfhir.core.data.repository.server.file.FileSyncRepository
import com.latticeonfhir.core.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.core.database.FhirAppDatabase
import com.latticeonfhir.core.model.enums.SyncStatusMessageEnum
import com.latticeonfhir.core.model.enums.WorkerStatus
import com.latticeonfhir.core.network.api.CVDApiService
import com.latticeonfhir.core.network.api.DispenseApiService
import com.latticeonfhir.core.network.api.FileUploadApiService
import com.latticeonfhir.core.network.api.LabTestAndMedRecordService
import com.latticeonfhir.core.network.api.PatientApiService
import com.latticeonfhir.core.network.api.PrescriptionApiService
import com.latticeonfhir.core.network.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.core.network.api.SymptomsAndDiagnosisService
import com.latticeonfhir.core.network.api.VaccinationApiService
import com.latticeonfhir.core.network.api.VitalApiService
import com.latticeonfhir.core.sharedpreference.preferencestorage.PreferenceStorage
import com.latticeonfhir.core.utils.file.DeleteFileManager
import com.latticeonfhir.sync.workmanager.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.sync.workmanager.sync.SyncService
import com.latticeonfhir.sync.workmanager.workmanager.utils.EventBus
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
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
    lateinit var vaccinationApiService: VaccinationApiService

    @Inject
    lateinit var fileUploadApiService: FileUploadApiService

    @Inject
    lateinit var deleteFileManager: DeleteFileManager

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
//    internal var photosWorkerStatus = MutableLiveData<WorkerStatus>()
    private val isSyncing = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        instance = this
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
            vaccinationApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            preferenceRepository,
            deleteFileManager,
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
            fhirAppDatabase.getDispenseDao(),
            fhirAppDatabase.getFileUploadDao(),
            fhirAppDatabase.getImmunizationRecommendationDao(),
            fhirAppDatabase.getImmunizationDao(),
            fhirAppDatabase.getManufacturerDao()
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
                        checkPhotoWorkerStatus(listOfErrors)
                    }
                }
            } finally {
                isSyncing.set(false)
            }
        }
    }

    private suspend fun checkPhotoWorkerStatus(
        listOfErrors: List<String>,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        withContext(mainDispatcher) {
            EventBus.photosWorkerStatus.observeForever { photosSyncStatus ->
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
                        Timber.d("manseeyy photos sync status ${EventBus.photosWorkerStatus}")
                    }
                }
            }
        }
    }

    companion object {
        lateinit var instance: FhirApp
            private set
    }

    init {
        instance = this
    }
}