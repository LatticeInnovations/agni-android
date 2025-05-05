package com.latticeonfhir.features.patient.ui.searchpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.model.enums.LastVisit.Companion.getLastVisitList
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.features.patient.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchPatientViewModel @Inject constructor() : BaseViewModel() {
    val onlyNumbers = Regex("^\\d+\$")
    var isLaunched by mutableStateOf(false)
    var fromHouseholdMember by mutableStateOf(false)
    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var patientName by mutableStateOf("")
    var patientId by mutableStateOf("")
    var gender by mutableStateOf("")
    var minAge by mutableStateOf("0")
    var maxAge by mutableStateOf("100")
    var visitSelected by mutableStateOf(getLastVisitList()[0])

    var range by mutableStateOf(minAge.toFloat()..maxAge.toFloat())

    var isNameValid by mutableStateOf(false)
    var isPatientIdValid by mutableStateOf(false)

    var address = Address()

    fun updateRange(minAge: String, maxAge: String) {
        val min: String = minAge.ifEmpty { "0" }
        val max: String = maxAge.ifEmpty { "0" }
        range = min.toFloat()..max.toFloat()
    }
}