package com.latticeonfhir.android.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.include
import com.google.android.fhir.search.search
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    application: Application,
    private val fhirEngine: FhirEngine
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf(Patient())

    var appointmentsIds = mutableListOf<String>()
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            FhirApp.runEnqueuedWorker(application)
        }
    }

    internal suspend fun getPatientData(): Patient {
        return fhirEngine.get(ResourceType.Patient, patient.logicalId) as Patient
    }

    internal fun getScheduledAppointmentsCount() {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.search<Encounter> {
                filter(
                    Encounter.SUBJECT, {
                        value = "${patient.fhirType()}/${patient.logicalId}"
                    }
                )
                include(ResourceType.Appointment, Encounter.APPOINTMENT) {
                    filter(
                        Appointment.STATUS, {
                            value = of("proposed")
                        }
                    )
                    filter(
                        Appointment.DATE, {
                            prefix = ParamPrefixEnum.GREATERTHAN
                            value = of(DateTimeType(Date()))
                        }
                    )
                }
            }.forEach { result ->
                result.included?.get(Encounter.APPOINTMENT.paramName)?.forEach { appointment ->
                    if (!appointmentsIds.contains(appointment.logicalId)) {
                        appointmentsIds.add(appointment.logicalId)
                    }
                }
            }
        }
    }
}