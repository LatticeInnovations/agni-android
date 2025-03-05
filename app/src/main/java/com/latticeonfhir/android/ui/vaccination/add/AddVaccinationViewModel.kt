package com.latticeonfhir.android.ui.vaccination.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddVaccinationViewModel @Inject constructor(

) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var listOfVaccines by mutableStateOf(
        listOf(
            "Bacille Calmette-Guérin (BCG)",
            "Oral Polio Vaccine (OPV)"
        )
    )
    var selectedVaccine by mutableStateOf("")
    var lotNo by mutableStateOf("")
    var dateOfExpiry: Date? by mutableStateOf(null)
    var showDatePicker by mutableStateOf(false)
    var selectedManufacturer by mutableStateOf("Select")
    var listOfManufacturer by mutableStateOf(
        listOf(
            "Select",
            "ABC",
            "BVC",
            "XYZ"
        )
    )
    var notes by mutableStateOf("")
    var showUploadSheet by mutableStateOf(false)
    var uploadedFile by mutableStateOf(listOf("abc.jpg", "hdshgf.img", "jsjdk.doc"))
    var showFileDeleteDialog by mutableStateOf(false)
}