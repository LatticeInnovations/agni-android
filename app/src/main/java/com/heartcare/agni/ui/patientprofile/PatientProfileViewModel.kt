package com.heartcare.agni.ui.patientprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel() {

    internal var isLaunched by mutableStateOf(false)
    internal var id by mutableStateOf("")

    internal var isProfileUpdated by mutableStateOf(false)
    internal var patientResponse by mutableStateOf<PatientResponse?>(null)

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }
}