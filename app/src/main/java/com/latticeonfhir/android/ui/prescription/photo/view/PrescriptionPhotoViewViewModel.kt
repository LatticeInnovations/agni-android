package com.latticeonfhir.android.ui.prescription.photo.view

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class PrescriptionPhotoViewViewModel: ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    var prescriptionPhotos by mutableStateOf(listOf<String>(
        "1717475093361.jpg"
    ))

    var selectedImageUri: Uri? by mutableStateOf(null)
}