package com.latticeonfhir.android.ui.patientprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : com.latticeonfhir.android.base.viewmodel.BaseViewModel() {

    internal var isLaunched by mutableStateOf(false)
    internal var id by mutableStateOf("")
    internal var firstName by mutableStateOf("")
    internal var middleName by mutableStateOf("")
    internal var lastName by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var email by mutableStateOf("")
    internal var dob by mutableStateOf("")
    internal var dobDay by mutableStateOf("")
    internal var dobMonth by mutableStateOf("")
    internal var dobYear by mutableStateOf("")
    internal var years by mutableStateOf("")
    internal var months by mutableStateOf("")
    internal var days by mutableStateOf("")
    internal var gender by mutableStateOf("")
    internal var passportId by mutableStateOf("")
    internal var voterId by mutableStateOf("")
    internal var patientId by mutableStateOf("")
    internal var isProfileUpdated by mutableStateOf(false)
    internal var identifier = mutableListOf<PatientIdentifier>()

    internal var homeAddress by mutableStateOf(Address())
    internal var patientResponse by mutableStateOf<PatientResponse?>(null)

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }

}