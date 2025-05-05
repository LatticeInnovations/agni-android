package com.latticeonfhir.features.vaccination.ui.add

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.file.DownloadedFileRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ManufacturerRepository
import com.latticeonfhir.core.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.core.database.entities.file.DownloadedFileEntity
import com.latticeonfhir.core.database.entities.file.FileUploadEntity
import com.latticeonfhir.core.database.entities.vaccination.ManufacturerEntity
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.core.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.core.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.core.utils.converters.responseconverter.Vaccination.toImmunizationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddVaccinationViewModel @Inject constructor(
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val manufacturerRepository: ManufacturerRepository,
    private val immunizationRepository: ImmunizationRepository,
    private val appointmentRepository: AppointmentRepository,
    private val fileSyncRepository: FileSyncRepository,
    private val downloadedFileRepository: DownloadedFileRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
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
    var isImagePreview by mutableStateOf(false)

    internal fun getImmunizationRecommendationAndManufacturerList(
        patientId: String
    ) {
        viewModelScope.launch {
            immunizationRecommendationList =
                immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            manufacturerList =
                listOf(selectedManufacturer) + manufacturerRepository.getAllManufacturers()
        }
    }

    internal fun addVaccination(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(
                    Date().toTodayStartDate(),
                    Date().toEndOfDay()
                )
                    .firstOrNull { appointmentEntity ->
                        appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                    }
            val takenOn = Date()
            val immunization = Immunization(
                id = UUIDBuilder.generateUUID(),
                vaccineName = selectedVaccine!!.name,
                vaccineSortName = selectedVaccine!!.shortName,
                vaccineCode = selectedVaccine!!.vaccineCode,
                lotNumber = lotNo,
                takenOn = takenOn,
                expiryDate = dateOfExpiry!!,
                manufacturer = if (selectedManufacturer.name != "Select") selectedManufacturer else null,
                notes = notes.trim().ifBlank { null },
                filename = uploadedFileUri.map { it.toFile().name },
                patientId = patient!!.id,
                appointmentId = appointmentResponseLocal!!.uuid
            )
            immunizationRepository.insertImmunization(
                immunization
            ).also {
                uploadedFileUri.map { it.toFile().name }.forEach { filename ->
                    insertInFileRepositories(filename)
                }
                // insert in generic
                genericRepository.insertImmunization(
                    immunization.copy(
                        patientId = patient!!.fhirId ?: patient!!.id,
                        appointmentId = appointmentResponseLocal.appointmentId
                            ?: appointmentResponseLocal.uuid
                    ).toImmunizationResponse()
                )
                checkAndUpdateAppointmentStatusToInProgress(
                    inProgressTime = takenOn,
                    patient = patient!!,
                    appointmentResponseLocal = appointmentResponseLocal,
                    appointmentRepository = appointmentRepository,
                    scheduleRepository = scheduleRepository,
                    genericRepository = genericRepository
                )
                updatePatientLastUpdated(
                    patient!!.id,
                    patientLastUpdatedRepository,
                    genericRepository
                )
            }
            added()
        }
    }

    private suspend fun insertInFileRepositories(filename: String) {
        fileSyncRepository.insertFile(
            FileUploadEntity(
                name = filename
            )
        )
        downloadedFileRepository.insertEntity(
            DownloadedFileEntity(
                name = filename
            )
        )
    }

    companion object {
        const val MAX_FILE_SIZE_IN_KB = 5120
    }
}