package com.latticeonfhir.android.ui.main

import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.person.PatientRepository
import com.latticeonfhir.android.data.server.model.PersonResponse
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncRepository.getListPersonData()
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            submitData(patientRepository.getPersonList()[0])
        }
    }

    private fun submitData(personResponse: PersonResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.updatePersonData(
                personResponse.copy(
                    firstName = "Naveen",
                    lastName = "Hawk"
                )
            )
        }
    }
}