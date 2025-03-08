package com.latticeonfhir.android.ui.vaccination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("All", "Missed", "Taken")

    var immunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())
    var missedImmunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())
    var takenImmunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())

    internal fun getImmunizationRecommendationAndImmunizationList(
        patientId: String
    ) {
        viewModelScope.launch {
            immunizationRecommendationList = immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            missedImmunizationRecommendationList = immunizationRecommendationList.filterList { vaccineStartDate < Date() && takenOn == null }.sortedBy { it.vaccineStartDate }
            takenImmunizationRecommendationList = immunizationRecommendationList.filterList { takenOn != null }.sortedByDescending { it.takenOn }
        }
    }


    companion object {
        const val MISSED = "missed"
        const val TAKEN = "taken"
    }
}