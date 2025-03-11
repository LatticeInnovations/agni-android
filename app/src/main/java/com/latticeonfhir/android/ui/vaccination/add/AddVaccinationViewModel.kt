package com.latticeonfhir.android.ui.vaccination.add

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.android.data.local.repository.vaccination.ManufacturerRepository
import com.latticeonfhir.android.data.local.roomdb.entities.vaccination.ManufacturerEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddVaccinationViewModel @Inject constructor(
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val manufacturerRepository: ManufacturerRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var immunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())
    var selectedVaccine by mutableStateOf<ImmunizationRecommendation?>(null)
    var selectedVaccineName by mutableStateOf("")
    var lotNo by mutableStateOf("")
    var dateOfExpiry: Date? by mutableStateOf(null)
    var showDatePicker by mutableStateOf(false)
    var selectedManufacturer by mutableStateOf(
        ManufacturerEntity(
            id = "0",
            name = "Select",
            type = "empty",
            active = false
        )
    )
    var manufacturerList by mutableStateOf(listOf<ManufacturerEntity>())
    var notes by mutableStateOf("")
    var showUploadSheet by mutableStateOf(false)
    var uploadedFileUri = mutableStateListOf<Uri>()
    var showFileDeleteDialog by mutableStateOf(false)

    var isImageCaptured by mutableStateOf(false)
    var selectedImageUri: Uri? by mutableStateOf(null)
    var isSelectedFromGallery by mutableStateOf(false)
    var tempFileName by mutableStateOf("")
    var isFileError by mutableStateOf(false)
    var selectedUriToDelete: Uri? by mutableStateOf(null)

    var displayCamera by mutableStateOf(false)
    var cameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    var flashOn by mutableStateOf(false)
    var showOpenSettingsDialog by mutableStateOf(false)

    internal fun getImmunizationRecommendationAndManufacturerList(
        patientId: String
    ) {
        viewModelScope.launch {
            immunizationRecommendationList = immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            manufacturerList = listOf(selectedManufacturer) + manufacturerRepository.getAllManufacturers()
        }
    }

    companion object {
        const val MAX_FILE_SIZE_IN_KB = 5120
    }
}