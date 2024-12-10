package com.latticeonfhir.android.ui.dispense.prescription.dispenseprescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.DispenseCategoryEnum
import com.latticeonfhir.android.data.local.enums.DispenseStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.dispense.DispenseModifiedInfo
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispenseRequest
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispensed
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.android.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DispensePrescriptionViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val dispenseRepository: DispenseRepository,
    private val medicationRepository: MedicationRepository,
    private val genericRepository: GenericRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    var recompose by mutableStateOf(false)
    var isLaunched by mutableStateOf(false)
    var prescription by mutableStateOf<DispenseAndPrescriptionRelation?>(null)
    var selectedMedicine by mutableStateOf(mutableSetOf<DispenseModifiedInfo>())
    var previousDispensed by mutableStateOf(listOf<DispensedPrescriptionInfo>())
    var medicationList by mutableStateOf(mutableListOf<DispenseModifiedInfo>())
    var dispenseNotes by mutableStateOf("")
    var showAddNoteDialog by mutableStateOf(false)
    var medToEdit by mutableStateOf<DispenseModifiedInfo?>(null)
    private var allMedications by mutableStateOf(listOf<MedicationStrengthRelation>())

    internal fun getData(
        prescriptionId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            prescription = dispenseRepository.getPrescriptionDispenseDataById(prescriptionId)
            allMedications = medicationRepository.getAllMedication()
            previousDispensed = dispenseRepository.getDispensedPrescriptionInfo(prescriptionId)
            val dispensedMedHashMap = hashMapOf<String, Int>()
            previousDispensed.forEach {
                it.medicineDispenseList.forEach { medDispensed ->
                    if (dispensedMedHashMap.containsKey(medDispensed.prescribedMedFhirId)) {
                        dispensedMedHashMap[medDispensed.prescribedMedFhirId] =
                            dispensedMedHashMap[medDispensed.prescribedMedFhirId]!! + medDispensed.qtyDispensed
                    } else {
                        dispensedMedHashMap[medDispensed.prescribedMedFhirId] =
                            medDispensed.qtyDispensed
                    }
                }
            }
            medicationList = prescription!!.prescriptionDirectionAndMedicineView.map {
                DispenseModifiedInfo(
                    qtyToBeDispensed = 0,
                    note = "",
                    medication = it,
                    isModified = false,
                    qtyLeft = it.prescriptionDirectionsEntity.qtyPrescribed - (dispensedMedHashMap[it.medicationEntity.medFhirId]
                        ?: 0)
                )
            }.toMutableList()
        }
    }

    internal fun getMedNameFromMedFhirId(medFhirId: String): MedicationStrengthRelation {
        return allMedications.first {
            it.medicationEntity.medFhirId == medFhirId
        }
    }

    internal fun dispenseMedication(
        dispensed: () -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            val patient = patientRepository.getPatientById(prescription!!.prescription.patientId)[0]
            var appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(
                    Date(Date().toTodayStartDate()).time,
                    Date(Date().toEndOfDay()).time
                ).firstOrNull { appointmentEntity ->
                    appointmentEntity.patientId == patient.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                }
            if (appointmentResponseLocal == null) {
                Queries.addPatientToQueue(
                    patient,
                    scheduleRepository,
                    genericRepository,
                    preferenceRepository,
                    appointmentRepository,
                    patientLastUpdatedRepository
                ) {
                    viewModelScope.launch(ioDispatcher) {
                        appointmentResponseLocal =
                            appointmentRepository.getAppointmentListByDate(
                                Date(Date().toTodayStartDate()).time,
                                Date(Date().toEndOfDay()).time
                            ).firstOrNull { appointmentEntity ->
                                appointmentEntity.patientId ==patient.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
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
        appointmentResponseLocal: AppointmentResponseLocal,
        dispensed: () -> Unit
    ) {
        val dispenseId = UUIDBuilder.generateUUID()
        val generatedOn = Date()
        val medDispenseRequest = MedicineDispenseRequest(
            dispenseId = dispenseId,
            generatedOn = generatedOn,
            patientId = prescription!!.prescription.patientFhirId
                ?: prescription!!.prescription.patientId,
            prescriptionFhirId = prescription!!.prescription.prescriptionFhirId
                ?: prescription!!.prescription.id,
            note = dispenseNotes.ifBlank { null },
            status = checkStatusOfPrescription(),
            medicineDispensedList = selectedMedicine.map {
                val isModified =
                    it.qtyToBeDispensed != it.medication.prescriptionDirectionsEntity.qtyPrescribed
                MedicineDispensed(
                    medDispenseUuid = UUIDBuilder.generateUUID(),
                    category = DispenseCategoryEnum.PRESCRIBED.value,
                    medFhirId = it.medication.medicationEntity.medFhirId,
                    medNote = it.note,
                    qtyDispensed = it.qtyToBeDispensed,
                    medReqFhirId = it.medication.prescriptionDirectionsEntity.medReqFhirId
                        ?: it.medication.prescriptionDirectionsEntity.id,
                    isModified = isModified,
                    modificationType = if (isModified) "OS" else null
                )
            },
            appointmentId = appointmentResponseLocal.appointmentId ?: appointmentResponseLocal.uuid
        )
        dispenseRepository.updateDispenseStatus(
            DispensePrescriptionEntity(
                patientId = prescription!!.prescription.patientId,
                prescriptionId = prescription!!.prescription.id,
                status = medDispenseRequest.status!!
            )
        )
        dispenseRepository.insertDispenseData(
            DispenseDataEntity(
                dispenseId = medDispenseRequest.dispenseId,
                dispenseFhirId = null,
                patientId = prescription!!.prescription.patientId,
                prescriptionId = prescription!!.prescription.id,
                generatedOn = medDispenseRequest.generatedOn,
                note = medDispenseRequest.note,
                appointmentId = appointmentResponseLocal.uuid
            ),
            medDispenseRequest.medicineDispensedList.map {
                MedicineDispenseListEntity(
                    medDispenseUuid = it.medDispenseUuid,
                    medDispenseFhirId = null,
                    dispenseId = medDispenseRequest.dispenseId,
                    patientId = prescription!!.prescription.patientId,
                    category = it.category,
                    qtyDispensed = it.qtyDispensed,
                    qtyPrescribed = prescription!!.prescriptionDirectionAndMedicineView.first { medicine ->
                        medicine.medicationEntity.medFhirId == it.medFhirId
                    }.prescriptionDirectionsEntity.qtyPrescribed,
                    date = medDispenseRequest.generatedOn,
                    isModified = it.isModified,
                    modificationType = it.modificationType,
                    medNote = it.medNote,
                    dispensedMedFhirId = it.medFhirId,
                    prescribedMedFhirId = it.medFhirId,
                    prescribedMedReqId = prescription!!.prescriptionDirectionAndMedicineView.first { prescription ->
                        prescription.medicationEntity.medFhirId == it.medFhirId
                    }.prescriptionDirectionsEntity.id
                )
            }
        )
        genericRepository.insertDispense(
            medicineDispenseRequest = medDispenseRequest
        )

        val patient = patientRepository.getPatientById(prescription!!.prescription.patientId)[0]

        checkAndUpdateAppointmentStatusToInProgress(
            inProgressTime = generatedOn,
            patient = patient,
            appointmentResponseLocal = appointmentResponseLocal,
            appointmentRepository = appointmentRepository,
            scheduleRepository = scheduleRepository,
            genericRepository = genericRepository
        )
        updatePatientLastUpdated(
            patient.id,
            patientLastUpdatedRepository,
            genericRepository
        )
        dispensed()
    }

    private fun checkStatusOfPrescription(): String {
        selectedMedicine.forEach { selectedMed ->
            medicationList.first { med ->
                med.medication.medicationEntity.medFhirId == selectedMed.medication.medicationEntity.medFhirId
            }.apply {
                qtyLeft -= qtyToBeDispensed
            }
        }
        Timber.d("manseeyy $medicationList")
        medicationList.forEach {
            if (it.qtyLeft > 0) return DispenseStatusEnum.PARTIALLY_DISPENSED.code
        }
        return DispenseStatusEnum.FULLY_DISPENSED.code
    }
}