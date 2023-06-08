package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var tabIndex by mutableIntStateOf(0)

    val tabs = listOf("Previous prescription", "Quick select")

    var patient by mutableStateOf<PatientResponse?>(null)

    var activeIngredientsList by mutableStateOf(listOf<String>())
    var selectedActiveIngredientsList by mutableStateOf(listOf<String>())
    var checkedActiveIngredient by mutableStateOf("")

    var medicationDirectionsList by mutableStateOf(listOf<MedicineDosageInstructionsEntity>())
    var medicationsResponseWithMedicationList by mutableStateOf(listOf<MedicationResponseWithMedication>())
    var medicationToEdit by mutableStateOf<MedicationResponseWithMedication?>(null)

    var searchQuery by mutableStateOf("")
    var previousSearchList by mutableStateOf(listOf<String>())
    var activeIngredientSearchList by mutableStateOf(listOf<String>())

    var previousPrescriptionList by mutableStateOf(listOf<PrescriptionAndMedicineRelation?>(null))

    internal fun getPreviousPrescription(patientId: String, previousPrescriptionList: (List<PrescriptionAndMedicineRelation>) -> Unit) {
        viewModelScope.launch {
            previousPrescriptionList(
                prescriptionRepository.getLastPrescription(patientId)
            )
        }
    }

    internal fun getActiveIngredients(activeIngredientsList: (List<String>) -> Unit) {
        viewModelScope.launch {
            activeIngredientsList(
                medicationRepository.getActiveIngredients()
            )
        }
    }

    internal fun getAllMedicationDirections(medicationDirectionsList: (List<MedicineDosageInstructionsEntity>) -> Unit) {
        viewModelScope.launch {
            medicationDirectionsList(
                medicationRepository.getAllMedicationDirections()
            )
        }
    }

    internal fun insertPrescription(inserted: (Long) -> Unit) {
        val medicationsList = mutableListOf<Medication>()
        medicationsResponseWithMedicationList.forEach {
            medicationsList.add(it.medication)
        }
        viewModelScope.launch {
            inserted(
                prescriptionRepository.insertPrescription(
                    PrescriptionResponseLocal(
                        patientId = patient?.id!!,
                        patientFhirId = patient?.fhirId,
                        generatedOn = Date(),
                        prescriptionId = UUIDBuilder.generateUUID(),
                        prescription = medicationsList
                    )
                )
            )
        }
    }

    internal fun getPreviousSearch(previousSearches: (List<String>) -> Unit) {
        viewModelScope.launch {
            previousSearches(
                searchRepository.getRecentActiveIngredientSearches()
            )
        }
    }

    internal fun insertRecentSearch(query: String) {
        viewModelScope.launch {
            searchRepository.insertRecentActiveIngredientSearch(query, Date())
        }
    }

    internal fun getActiveIngredientSearchList(activeIngredient: String, searchList: (List<String>) -> Unit) {
        viewModelScope.launch {
            searchList(
                searchRepository.searchActiveIngredients(activeIngredient)
            )
        }
    }
}