package com.latticeonfhir.features.vaccination.ui.view

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.model.local.vaccination.Immunization
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRepository
import com.latticeonfhir.core.model.server.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewVaccinationViewModel@Inject constructor(
    private val immunizationRepository: ImmunizationRepository
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var immunization by mutableStateOf<Immunization?>(null)
    var immunizationRecommendation by mutableStateOf<ImmunizationRecommendation?>(null)
    var patient by mutableStateOf<PatientResponse?>(null)
    var selectedUri by mutableStateOf<Uri?>(null)

    fun getImmunizationByTime(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        createdOn: Date
    ) {
        viewModelScope.launch(ioDispatcher) {
            immunization = immunizationRepository.getImmunizationByTime(createdOn)
        }
    }
}