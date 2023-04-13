package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class SuggestionsScreenViewModel: BaseViewModel() {
    var showConnectDialog by mutableStateOf(false)

    val patient = PatientResponse(
        id = "d138ada3-82f7-4b96-914f-decd5933b61d",
        firstName = "Mansi",
        middleName = null,
        lastName = "Kalra",
        active = true,
        birthDate = "2001-01-23",
        email = null,
        fhirId = null,
        gender = "female",
        mobileNumber = 9999999999,
        permanentAddress = PatientAddressResponse(
            addressLine1 = "hbghhg",
            addressLine2 = null,
            postalCode = "999999",
            city = "vggh",
            country = "India",
            district = null,
            state = "Uttarakhand"
        ),
        identifier = listOf(
            PatientIdentifier(
                code = null,
                identifierType = "https://www.apollohospitals.com/",
                identifierNumber = "XXXXXXXXXX"
            )
        )
    )

    val suggestedMembersList = listOf(patient, patient, patient)

    //var suggestedMembersList by mutableStateOf(listOf<PatientResponse>())
}