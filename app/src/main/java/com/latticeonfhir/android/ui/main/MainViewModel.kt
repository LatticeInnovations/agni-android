package com.latticeonfhir.android.ui.main

import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository
) : BaseViewModel() {

    private val personId = UUIDBuilder.generateUUID()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncRepository.getListPatientData()
            val map = mutableMapOf<String,ChangeRequest>()
            map["middleName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Hawk")
            map["lastName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Singh")
            genericRepository.insertGenericEntity(
                patientId = personId,
                map = map,
                typeEnum = GenericTypeEnum.PATIENT
            )
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            submitData(patientRepository.getPatientList()[0])
        }
    }

    private fun submitData(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.updatePatientData(
                patientResponse.copy(
                    firstName = "Naveen",
                    lastName = "Hawk"
                )
            )
            val map = mutableMapOf<String,ChangeRequest>()
            map["firstName"] = ChangeRequest(operation = ChangeTypeEnum.ADD.value, value = "Naveen")
            genericRepository.insertGenericEntity(
                patientId = personId,
                map = map,
                typeEnum = GenericTypeEnum.PATIENT
            )
        }
    }
}