package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository
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

    var searchQuery by mutableStateOf("")
    var previousSearchList = mutableStateListOf(
        "List Item 1",
        "List Item 2",
        "List Item 3",
        "List Item 4",
        "List Item 5",
    )

    internal fun getActiveIngredients(){
        viewModelScope.launch {
            activeIngredientsList = medicationRepository.getActiveIngredients()
        }
    }
}