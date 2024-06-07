package com.latticeonfhir.android.ui.prescription.photo.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoViewViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var isLongPressed by mutableStateOf(false)
    var isTapped by mutableStateOf(false)
    var showNoteDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var displayNote by mutableStateOf(false)
    var prescriptionPhotos by mutableStateOf(listOf<File>())

    var selectedFile: File? by mutableStateOf(null)

    internal fun getPastPrescription() {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionPhotos = prescriptionRepository.getLastPhotoPrescription(patient!!.id)
        }
    }

    internal fun addNoteToPrescription(
        note: String,
        added: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                patient!!.id,
                dateOfFile.toTodayStartDate(),
                dateOfFile.toEndOfDay()
            )
            prescriptionRepository.insertPrescriptionPhotos(
                PrescriptionPhotoEntity(
                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
                    fileName = selectedFile!!.filename,
                    note = note
                )
            )
            if (prescriptionPhotoResponse.prescriptionFhirId == null) {
                // insert generic post
                val updatedPrescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                    patient!!.id,
                    dateOfFile.toTodayStartDate(),
                    dateOfFile.toEndOfDay()
                )
                genericRepository.insertPhotoPrescription(
                    updatedPrescriptionPhotoResponse
                )
            } else {
                // insert generic patch
            }
            getPastPrescription()
            added()
        }
    }

    internal fun deletePrescription(
        deleted: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                patient!!.id,
                dateOfFile.toTodayStartDate(),
                dateOfFile.toEndOfDay()
            )
            // delete from local db
            prescriptionRepository.deletePrescriptionPhotos(
                PrescriptionPhotoEntity(
                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
                    fileName = selectedFile!!.filename,
                    note = selectedFile!!.note
                )
            )
            // update in generic
            if (prescriptionPhotoResponse.prescriptionFhirId == null) {
                // insert generic post
                val updatedPrescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                    patient!!.id,
                    dateOfFile.toTodayStartDate(),
                    dateOfFile.toEndOfDay()
                )
                genericRepository.insertPhotoPrescription(
                    updatedPrescriptionPhotoResponse
                )
            } else {
                // insert generic patch
            }
            getPastPrescription()
            deleted()
        }
    }
}