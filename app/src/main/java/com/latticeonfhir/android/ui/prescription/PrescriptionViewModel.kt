package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationRequestAndMedication
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getMedicationList
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getTodayAppointmentAndEncounterOfPatient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
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

    var patient by mutableStateOf(Patient())

    var activeIngredientsList by mutableStateOf(listOf<String>())
    var selectedActiveIngredientsList by mutableStateOf(listOf<String>())
    var checkedActiveIngredient by mutableStateOf("")

    var medicationRequestAndMedicationList by mutableStateOf(listOf<MedicationRequestAndMedication>())
    var medicationToEdit by mutableStateOf<MedicationRequestAndMedication?>(null)

    var searchQuery by mutableStateOf("")
    var previousSearchList by mutableStateOf(listOf<String>())
    var activeIngredientSearchList by mutableStateOf(listOf<String>())

    var previousPrescriptionList by mutableStateOf(listOf<PrescriptionAndMedicineRelation?>(null))

    internal var todayAppointment by mutableStateOf<Appointment?>(null)
    internal var todayEncounter by mutableStateOf<Encounter?>(null)

    var medicationList: Deferred<List<org.hl7.fhir.r4.model.Medication>> =
        viewModelScope.async(Dispatchers.IO) {
            getMedicationList(fhirEngine)
        }

    internal suspend fun getPatientTodayAppointment() {
        getTodayAppointmentAndEncounterOfPatient(
            fhirEngine,
            patient.logicalId
        )?.apply {
            todayEncounter = resource
            todayAppointment =
                included?.get(Encounter.APPOINTMENT.paramName)?.get(0) as Appointment
        }
    }

    internal fun getPreviousPrescription(
        patientId: String,
        previousPrescriptionList: (List<PrescriptionAndMedicineRelation>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            previousPrescriptionList(
                prescriptionRepository.getLastPrescription(patientId)
            )
        }
    }

    internal fun getActiveIngredients() {
        viewModelScope.launch(Dispatchers.IO) {
            medicationList.await().forEach { medication ->
                var activeIngredient = ""
                medication.ingredient.forEach { ing ->
                    activeIngredient += if (activeIngredient.isBlank()) ing.itemCodeableConcept.codingFirstRep.display else "+${ing.itemCodeableConcept.codingFirstRep.display}"
                }
                if (!activeIngredientsList.contains(activeIngredient))
                    activeIngredientsList = activeIngredientsList + listOf(activeIngredient)
            }
        }
    }

    internal fun insertPrescription(
        date: Date = Date(),
        prescriptionId: String = UUIDBuilder.generateUUID(),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: (Long) -> Unit
    ) {
//        val medicationsList = mutableListOf<Medication>()
//        medicationsResponseWithMedicationList.forEach { medicationResponseWithMedication ->
//            medicationsList.add(
//                medicationResponseWithMedication.medication
//            )
//        }
        viewModelScope.launch {
//            inserted(withContext(ioDispatcher) {
//                insertPrescriptionInDB(date, prescriptionId, medicationsList).also {
//                    insertGenericEntityInDB(date, prescriptionId, medicationsList)
//                    appointmentRepository.updateAppointment(
//                        todayAppointment!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
//                            .also { updatedAppointmentResponse ->
//                                todayAppointment = updatedAppointmentResponse
//                            }
//                    )
//                }
//            })
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
                patientId = patient.id,
                patientFhirId = patient.logicalId,
                generatedOn = date,
                prescriptionId = prescriptionId,
                prescription = medicationsList,
                appointmentId = todayAppointment!!.logicalId
            )
        )
    }

//    private suspend fun insertGenericEntityInDB(
//        date: Date,
//        prescriptionId: String,
//        medicationsList: List<Medication>
//    ): Long {
//        return genericRepository.insertPrescription(
//            PrescriptionResponse(
//                patientFhirId = patient.logicalId,
//                generatedOn = date,
//                prescriptionId = prescriptionId,
//                prescription = medicationsList.map { medication ->
//                    medication.copy(
//                        timing = timingList.await()
//                            .find { timing -> timing.medicalDosage == medication.timing }?.medicalDosageId
//                    )
//                },
//                prescriptionFhirId = null,
//                appointmentUuid = todayAppointment!!.logicalId,
//                appointmentId = todayAppointment!!.logicalId
//            )
//        )
//    }
}