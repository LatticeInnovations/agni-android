package com.latticeonfhir.android.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class PrescriptionViewModel : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var tabIndex by mutableStateOf(0)

    val tabs = listOf("Previous prescription", "Quick select")

    var patient by mutableStateOf<PatientResponse?>(null)

    var compoundList = mutableStateListOf(
        "Epinephrine (adrenaline)",
        "Enalapril",
        "Lisinopril",
        "Metformin",
        "Insulin glargine",
        "Liraglutide",
        "Sitagliptin",
        "Albuterol",
        "Fluticasone",
        "Montelukast"
    )
    var selectedCompoundList = mutableStateListOf<String>()
    var checkedCompound by mutableStateOf("")

    var searchQuery by mutableStateOf("")
    var previousSearchList = mutableStateListOf(
        "List Item 1",
        "List Item 2",
        "List Item 3",
        "List Item 4",
        "List Item 5",
    )
}