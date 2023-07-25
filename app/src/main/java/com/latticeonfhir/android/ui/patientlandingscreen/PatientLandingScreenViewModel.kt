package com.latticeonfhir.android.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var logoutUser by mutableStateOf(false)
    var logoutReason by mutableStateOf("")

    var appointmentsCount by mutableStateOf(0)
    var isFabSelected by mutableStateOf(false)

    private val workRequestBuilders: WorkRequestBuilders by lazy { (application as FhirApp).geWorkRequestBuilder() }

    internal fun downloadPrescriptions(patientFhirId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            workRequestBuilders.downloadPrescriptionWorker(patientFhirId) { isErrorReceived, errorMsg ->
                if (isErrorReceived){
                    logoutUser = true
                    logoutReason = errorMsg
                }
            }
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }
}