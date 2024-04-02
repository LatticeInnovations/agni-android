package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationRequestAndMedication
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
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
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var isPrescribing by mutableStateOf(false)

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
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            fhirEngine.create(*medicationRequestAndMedicationList.map { it.medicationRequest }.toTypedArray())
            fhirEngine.update(
                todayEncounter!!.apply {
                    status = Encounter.EncounterStatus.INPROGRESS
                }
            )
            inserted()
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
                searchRepository.insertRecentActiveIngredientSearch(query.trim(), date)
            )
        }
    }

    internal fun getActiveIngredientSearchList(
        activeIngredient: String,
        searchList: (List<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            searchList(
                searchRepository.searchActiveIngredients(activeIngredient, activeIngredientsList)
            )
        }
    }
}