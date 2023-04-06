package com.latticeonfhir.android.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.BackoffCriteria
import com.latticeonfhir.android.service.workmanager.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.RepeatInterval
import com.latticeonfhir.android.service.workmanager.RetryConfiguration
import com.latticeonfhir.android.service.workmanager.Sync
import com.latticeonfhir.android.service.workmanager.SyncJobStatus
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val searchRepository: SearchRepository,
    application: Application
) : BaseAndroidViewModel(application) {

    private val personId = UUIDBuilder.generateUUID()

    init {
        FhirApp.syncRepository = syncRepository
        val list = mutableListOf<ChangeRequest>()
        list.add(
            ChangeRequest(
                key = "http://hospital.smarthealthit.org",
                "add", PatientIdentifier(
                    identifierType = "http://hospital.smarthealthit.org",
                    identifierNumber = "22483974-herh8478374-fhdj7866",
                    code = "MR"
                )
            )
        )

        list.add(
            ChangeRequest(
                key = "https://www.pan.utiitsl.com",
                operation = "add",
                PatientIdentifier(
                    identifierType = "https://www.pan.utiitsl.com",
                    identifierNumber = "SATIH3787N",
                    code = null
                )
            )
        )

        viewModelScope.launch(Dispatchers.IO) {
            Sync.periodicSync<PatientUploadSyncWorkerImpl>(
                getApplication<Application?>().baseContext,
                PeriodicSyncConfiguration(
                    syncConstraints = Constraints.Builder().build(),
                    repeat = RepeatInterval(10, TimeUnit.SECONDS),
                    retryConfiguration = RetryConfiguration(
                        BackoffCriteria(
                            BackoffPolicy.LINEAR,
                            10,
                            TimeUnit.SECONDS
                        ), 5
                    )
                )
            ).collectLatest {
                Timber.d("Collected Latest ${it.timestamp}")
            }

            Sync.periodicSync<PatientDownloadSyncWorkerImpl>(
                getApplication<Application?>().applicationContext, PeriodicSyncConfiguration(
                    syncConstraints = Constraints.Builder().build(),
                    repeat = RepeatInterval(10, TimeUnit.SECONDS),
                    retryConfiguration = RetryConfiguration(
                        BackoffCriteria(
                            BackoffPolicy.LINEAR,
                            10,
                            TimeUnit.SECONDS
                        ), 5
                    )
                )
            )

//            syncRepository.getAndInsertListPatientData()
            val map = mutableMapOf<String, Any>()
            map["id"] = 109
            map["resourceType"] = "Patient"
            map["middleName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Hawk")
            map["lastName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Singh")
            map["identifier"] = list
            map["active"] = true
            genericRepository.insertOrUpdatePatchEntity(
                patientId = personId,
                map = map,
                typeEnum = GenericTypeEnum.PATIENT
            )
        }
    }

    internal fun getUserData() {
        viewModelScope.launch {
            patientRepository.getPatientList()
            submitData()
        }
    }

    private fun submitData() {
        viewModelScope.launch(Dispatchers.IO) {
//            patientRepository.updatePatientData(
//                patientResponse.copy(
//                    firstName = "Naveen",
//                    lastName = "Hawk"
//                )
//            )
//            val list = mutableListOf<ChangeRequest>()
//            list.add(
//                ChangeRequest(
//                    key = "http://hospital.smarthealthit.org",
//                    operation = "remove",
//                    PatientIdentifier(
//                        identifierType = "http://hospital.smarthealthit.org",
//                        identifierNumber = "22483974-herh8478374-fhdj7867",
//                        code = "MR"
//                    )
//                )
//            )
//            val map = mutableMapOf<String, Any>()
//            map["firstName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Naveen")
//            map["identifier"] = list
//            val c = genericRepository.insertOrUpdatePatchEntity(
//                patientId = personId,
//                map = map,
//                typeEnum = GenericTypeEnum.PATIENT
//            )
//            if(c > 0) syncRepository.sendPersonPatchData()
        }
    }
}