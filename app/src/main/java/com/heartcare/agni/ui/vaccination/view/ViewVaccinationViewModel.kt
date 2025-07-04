package com.heartcare.agni.ui.vaccination.view

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.data.local.model.vaccination.Immunization
import com.heartcare.agni.data.local.model.vaccination.ImmunizationRecommendation
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
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