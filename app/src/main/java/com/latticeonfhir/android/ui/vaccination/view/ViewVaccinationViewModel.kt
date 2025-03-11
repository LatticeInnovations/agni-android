package com.latticeonfhir.android.ui.vaccination.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewVaccinationViewModel@Inject constructor(
    private val immunizationRepository: ImmunizationRepository
): ViewModel() {
    var isLaunched by mutableStateOf(false)
    var immunization by mutableStateOf<Immunization?>(null)
    var immunizationRecommendation by mutableStateOf<ImmunizationRecommendation?>(null)
    var patient by mutableStateOf<PatientResponse?>(null)

    fun getImmunizationByTime(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        createdOn: Date
    ) {
        viewModelScope.launch(ioDispatcher) {
            immunization = immunizationRepository.getImmunizationByTime(createdOn)
        }
    }
}