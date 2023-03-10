package com.latticeonfhir.android.ui.main

import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val patientRepository: PatientRepository,
) : BaseViewModel() {

    private val changeMap = mutableMapOf<String,ChangeRequest>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncRepository.getListPatientData()
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            submitData(patientRepository.getPatientList()[0])
        }
    }

    private fun submitData(PatientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.updatePatientData(
                PatientResponse.copy(
                    firstName = "Naveen",
                    lastName = "Hawk"
                )
            )
        }
    }
}