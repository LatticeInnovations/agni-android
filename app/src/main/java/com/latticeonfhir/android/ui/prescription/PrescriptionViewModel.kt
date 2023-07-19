package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var tabIndex by mutableStateOf(0)

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
                medicationRepository.getAllMedicationDirections()
            )
        }
    }

    internal fun insertPrescription(inserted: (Long) -> Unit) {
        val medicationsList = mutableListOf<Medication>()
        medicationsResponseWithMedicationList.forEach {
            medicationsList.add(it.medication)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val prescriptionId = UUIDBuilder.generateUUID()
            inserted(
                prescriptionRepository.insertPrescription(
                    PrescriptionResponseLocal(
                        patientId = patient!!.id,
                        patientFhirId = patient?.fhirId,
                        generatedOn = Date(),
                        prescriptionId = prescriptionId,
                        prescription = medicationsList
                    )
                ).also {
                    genericRepository.insertPrescription(
                        PrescriptionResponse(
                            patientFhirId = patient!!.fhirId ?: patient!!.id,
                            generatedOn = Date(),
                            prescriptionId = prescriptionId,
                            prescription = medicationsList,
                            prescriptionFhirId = null
                        )
                    )
                }
            )
        }
    }

    internal fun getPreviousSearch(previousSearches: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            previousSearches(
                searchRepository.getRecentActiveIngredientSearches()
            )
        }
    }

    internal fun insertRecentSearch(query: String, inserted: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            inserted(
                searchRepository.insertRecentActiveIngredientSearch(query)
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