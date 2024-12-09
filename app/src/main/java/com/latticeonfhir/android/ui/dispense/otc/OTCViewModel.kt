package com.latticeonfhir.android.ui.dispense.otc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.DispenseCategoryEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispenseRequest
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispensed
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.android.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OTCViewModel@Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val dispenseRepository: DispenseRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository
): ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var selectedMedicine by mutableStateOf<MedicationStrengthRelation?>(null)
    var qtyPrescribed by mutableStateOf("")
    var notes by mutableStateOf("")
    var allMedications by mutableStateOf(listOf<MedicationStrengthRelation>())
    var isError by mutableStateOf(false)

    internal fun getOTCMedications() {
        viewModelScope.launch(Dispatchers.IO) {
            allMedications = medicationRepository.getOTCMedication()
        }
    }

    internal fun dispenseMedication(dispensed: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(
                    Date(Date().toTodayStartDate()).time,
                    Date(Date().toEndOfDay()).time
                ).firstOrNull { appointmentEntity ->
                        appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                    }
            if (appointmentResponseLocal == null) {
                Queries.addPatientToQueue(
                    patient!!,
                    scheduleRepository,
                    genericRepository,
                    preferenceRepository,
                    appointmentRepository,
                    patientLastUpdatedRepository
                ) {
                    viewModelScope.launch(Dispatchers.IO) {
                        appointmentResponseLocal =
                            appointmentRepository.getAppointmentListByDate(
                                Date(Date().toTodayStartDate()).time,
                                Date(Date().toEndOfDay()).time
                            ).firstOrNull { appointmentEntity ->
                                appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                            }
                        createDispense(
                            appointmentResponseLocal!!,
                            dispensed
                        )
                    }
                }
            } else {
                createDispense(
                    appointmentResponseLocal!!,
                    dispensed
                )
            }
        }
    }

    private suspend fun createDispense(
        appointmentResponse: AppointmentResponseLocal,
        dispensed: () -> Unit
    ) {
        val dispenseId = UUIDBuilder.generateUUID()
        val generatedOn = Date()
        val medDispenseRequest = MedicineDispenseRequest(
            dispenseId = dispenseId,
            generatedOn = generatedOn,
            patientId = patient!!.fhirId ?: patient!!.id,
            prescriptionFhirId = null,
            note = null,
            status = null,
            medicineDispensedList = listOf(
                MedicineDispensed(
                    medDispenseUuid = UUIDBuilder.generateUUID(),
                    category = DispenseCategoryEnum.OTC.value,
                    medFhirId = selectedMedicine!!.medicationEntity.medFhirId,
                    medNote = notes.ifBlank { null },
                    qtyDispensed = qtyPrescribed.toInt(),
                    medReqFhirId = null,
                    isModified = false,
                    modificationType = null
                )
            ),
            appointmentId = appointmentResponse.appointmentId
                ?: appointmentResponse.uuid
        )
        dispenseRepository.insertDispenseData(
            DispenseDataEntity(
                dispenseId = medDispenseRequest.dispenseId,
                dispenseFhirId = null,
                patientId = patient!!.id,
                prescriptionId = null,
                generatedOn = medDispenseRequest.generatedOn,
                note = null,
                appointmentId = appointmentResponse.uuid
            ),
            medDispenseRequest.medicineDispensedList.map {
                MedicineDispenseListEntity(
                    medDispenseUuid = it.medDispenseUuid,
                    medDispenseFhirId = null,
                    dispenseId = medDispenseRequest.dispenseId,
                    patientId = patient!!.id,
                    category = it.category,
                    qtyDispensed = it.qtyDispensed,
                    qtyPrescribed = it.qtyDispensed,
                    date = medDispenseRequest.generatedOn,
                    isModified = false,
                    modificationType = it.modificationType,
                    medNote = it.medNote,
                    dispensedMedFhirId = it.medFhirId,
                    prescribedMedFhirId = it.medFhirId,
                    prescribedMedReqId = null
                )
            }
        )
        genericRepository.insertDispense(
            medicineDispenseRequest = medDispenseRequest
        )
        checkAndUpdateAppointmentStatusToInProgress(
            inProgressTime = generatedOn,
            patient = patient!!,
            appointmentResponseLocal = appointmentResponse,
            appointmentRepository = appointmentRepository,
            scheduleRepository = scheduleRepository,
            genericRepository = genericRepository
        )
        updatePatientLastUpdated(
            patient!!.id,
            patientLastUpdatedRepository,
            genericRepository
        )
        dispensed()
    }
}