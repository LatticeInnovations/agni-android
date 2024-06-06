package com.latticeonfhir.android.ui.prescription.photo.view

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoViewViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository
): ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var isLongPressed by mutableStateOf(false)
    var isTapped by mutableStateOf(false)
    var showNoteDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var displayNote by mutableStateOf(false)
    var note = "Ask CDCR San Quintin State Prison 2008. We installed Purex dispensers throughout the prison to comba"

    var prescriptionPhotos by mutableStateOf(listOf<String>())

    var selectedImageUri: Uri? by mutableStateOf(null)

    internal fun getPastPrescription() {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionPhotos = prescriptionRepository.getLastPhotoPrescription(patient!!.id)
        }
    }
}