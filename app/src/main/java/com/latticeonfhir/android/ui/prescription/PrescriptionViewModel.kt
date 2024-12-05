package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.MedicationLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    var activeIngredientsList by mutableStateOf(listOf<String>())
    var selectedActiveIngredientsList by mutableStateOf(listOf<String>())
    var checkedActiveIngredient by mutableStateOf("")

    var medicationDirectionsList by mutableStateOf(listOf<MedicineTimingEntity>())
    var medicationsResponseWithMedicationList by mutableStateOf(listOf<MedicationResponseWithMedication>())
    var medicationToEdit by mutableStateOf<MedicationResponseWithMedication?>(null)

    var searchQuery by mutableStateOf("")
    var previousSearchList by mutableStateOf(listOf<String>())
    var activeIngredientSearchList by mutableStateOf(listOf<String>())

    var previousPrescriptionList by mutableStateOf(listOf<PrescriptionAndMedicineRelation?>(null))

    internal var appointmentResponseLocal: AppointmentResponseLocal? = null

    private var timingList: Deferred<List<MedicineTimingEntity>> =
        viewModelScope.async(Dispatchers.IO) {
            medicationRepository.getAllMedicationDirections()
        }

    internal fun getPatientTodayAppointment(startDate: Date, endDate: Date, patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
                    .firstOrNull { appointmentEntity ->
                        appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                    }
        }
    }

    internal fun getActiveIngredients(activeIngredientsList: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            activeIngredientsList(
                medicationRepository.getActiveIngredients()
            )
        }
    }

    internal fun getAllMedicationDirections(medicationDirectionsList: (List<MedicineTimingEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            medicationDirectionsList(
                timingList.await()
            )
        }
    }

    internal fun insertPrescription(
        date: Date = Date(),
        prescriptionId: String = UUIDBuilder.generateUUID(),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: (Long) -> Unit
    ) {
        val medicationsList = mutableListOf<Medication>()
        medicationsResponseWithMedicationList.forEach { medicationResponseWithMedication ->
            medicationsList.add(
                medicationResponseWithMedication.medication
            )
        }
        viewModelScope.launch {
            inserted(withContext(ioDispatcher) {
                insertPrescriptionInDB(date, prescriptionId, medicationsList).also {
                    insertGenericEntityInDB(date, prescriptionId, medicationsList)
                    appointmentRepository.updateAppointment(
                        appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                            .also { updatedAppointmentResponse ->
                                appointmentResponseLocal = updatedAppointmentResponse
                            }
                    )
                }
            })
        }
    }

    internal fun getPreviousSearch(previousSearches: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            previousSearches(
                searchRepository.getRecentActiveIngredientSearches()
            )
        }
    }

    internal fun insertRecentSearch(query: String, date: Date = Date(), inserted: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            inserted(
                searchRepository.insertRecentActiveIngredientSearch(query, date)
            )
        }
    }

    internal fun getActiveIngredientSearchList(
        activeIngredient: String,
        searchList: (List<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            searchList(
                searchRepository.searchActiveIngredients(activeIngredient)
            )
        }
    }

    private suspend fun insertPrescriptionInDB(
        date: Date,
        prescriptionId: String,
        medicationsList: List<Medication>
    ): Long {
        return prescriptionRepository.insertPrescription(
            PrescriptionResponseLocal(
                patientId = patient!!.id,
                patientFhirId = patient?.fhirId,
                generatedOn = date,
                prescriptionId = prescriptionId,
                prescription = medicationsList.map {
                    MedicationLocal(
                        medUnit = "",
                        medName = "",
                        doseForm = it.doseForm,
                        qtyPrescribed = it.qtyPrescribed,
                        frequency = it.frequency,
                        duration = it.duration,
                        medFhirId = it.medFhirId,
                        medReqUuid = it.medReqUuid,
                        medReqFhirId = it.medReqFhirId,
                        note = it.note,
                        qtyPerDose = it.qtyPerDose,
                        timing = it.timing
                    )
                },
                appointmentId = appointmentResponseLocal!!.uuid
            )
        )
    }

    private suspend fun insertGenericEntityInDB(
        date: Date,
        prescriptionId: String,
        medicationsList: List<Medication>
    ): Long {
        return genericRepository.insertPrescription(
            PrescriptionResponse(
                patientFhirId = patient!!.fhirId ?: patient!!.id,
                generatedOn = date,
                prescriptionId = prescriptionId,
                prescription = medicationsList.map { medication ->
                    medication.copy(
                        timing = timingList.await()
                            .find { timing -> timing.medicalDosage == medication.timing }?.medicalDosageId
                    )
                },
                prescriptionFhirId = null,
                appointmentUuid = appointmentResponseLocal!!.uuid,
                appointmentId = appointmentResponseLocal!!.appointmentId
                    ?: appointmentResponseLocal!!.uuid
            )
        )
    }
}