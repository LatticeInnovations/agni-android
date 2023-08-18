package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
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
import kotlinx.coroutines.Dispatchers
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

    val tabs = listOf("Previous prescription", "Quick select")

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
    private lateinit var timingList: List<MedicineTimingEntity>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            timingList = medicationRepository.getAllMedicationDirections()
        }
    }

    internal fun getPatientTodayAppointment(startDate: Date, endDate: Date, patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentResponseLocal = appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time).firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
            }
        }
    }

    internal fun getPreviousPrescription(
        patientId: String,
        previousPrescriptionList: (List<PrescriptionAndMedicineRelation>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            previousPrescriptionList(
                prescriptionRepository.getLastPrescription(patientId)
                    .map { prescriptionAndMedicineRelation ->
                        prescriptionAndMedicineRelation.prescriptionDirectionAndMedicineView.map { prescriptionAndMedicineView ->
                            prescriptionAndMedicineView.prescriptionDirectionsEntity.copy(
                                timing = timingList.find { medicineTimingEntity ->
                                    medicineTimingEntity.medicalDosageId == prescriptionAndMedicineView.prescriptionDirectionsEntity.timing
                                }?.medicalDosage
                            )
                        }
                        prescriptionAndMedicineRelation
                    }
            )
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
                timingList
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
                medicationResponseWithMedication.medication.copy(
                    timing = medicationDirectionsList.find { timing -> timing.medicalDosage == medicationResponseWithMedication.medication.timing }?.medicalDosageId
                )
            )
        }
        viewModelScope.launch {
            inserted(withContext(ioDispatcher) {
                prescriptionRepository.insertPrescription(
                    PrescriptionResponseLocal(
                        patientId = patient!!.id,
                        patientFhirId = patient?.fhirId,
                        generatedOn = date,
                        prescriptionId = prescriptionId,
                        prescription = medicationsList,
                        appointmentId = appointmentResponseLocal!!.uuid
                    )
                ).also {
                    genericRepository.insertPrescription(
                        PrescriptionResponse(
                            patientFhirId = patient!!.fhirId ?: patient!!.id,
                            generatedOn = date,
                            prescriptionId = prescriptionId,
                            prescription = medicationsList,
                            prescriptionFhirId = null,
                            appointmentId = appointmentResponseLocal!!.appointmentId
                                ?: appointmentResponseLocal!!.uuid
                        )
                    )
                    appointmentRepository.updateAppointment(appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value))
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
}