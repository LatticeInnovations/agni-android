package com.latticeonfhir.android.ui.patientregistration.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.Reference
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationPreviewViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var showLoader by mutableStateOf(false)

    var patient by mutableStateOf<Patient?>(null)

    internal var openDialog by mutableStateOf(false)

    internal var fromHouseholdMember by mutableStateOf(false)
    internal var patientFrom by mutableStateOf(Patient())
    internal var patientFromId by mutableStateOf("")
    internal var relativeId by mutableStateOf(UUIDBuilder.generateUUID())
    internal var relation by mutableStateOf("")

    internal fun addPatient(created: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val person = Person().apply {
                id = UUIDBuilder.generateUUID()
                identifier = patient!!.identifier
                link.add(
                    Person.PersonLinkComponent()
                        .setTarget(Reference("${patient!!.fhirType()}/${patient!!.logicalId}"))
                        .setAssurance(Person.IdentityAssuranceLevel.LEVEL3)
                )
            }
            val i = fhirEngine.create(patient!!, person)
            if (i.isNotEmpty()) {
                created()
            }
        }
    }
}