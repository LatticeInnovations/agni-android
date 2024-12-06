package com.latticeonfhir.android.ui.dispense

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrugDispenseViewModel @Inject constructor(
    private val dispenseRepository: DispenseRepository,
    private val medicationRepository: MedicationRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    val tabs = listOf("Prescription", "Dispense log")
    var prescriptionSelected by mutableStateOf<DispenseAndPrescriptionRelation?>(null)
    var previousPrescriptionList by mutableStateOf(listOf<DispenseAndPrescriptionRelation>())
    var previousDispensed by mutableStateOf(listOf<DispensedPrescriptionInfo>())
    private var allMedications by mutableStateOf(listOf<MedicationStrengthRelation>())
    var isOTCDispensed by mutableStateOf(false)

    internal fun getPrescriptionDispenseData(
        patientId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            previousPrescriptionList = dispenseRepository.getPrescriptionDispenseData(patientId)
            allMedications = medicationRepository.getAllMedication()
            previousDispensed = dispenseRepository.getDispensedPrescriptionInfoByPatientId(patientId)
        }
    }

    internal fun getMedNameFromMedFhirId(medFhirId: String): MedicationStrengthRelation {
        return allMedications.first {
            it.medicationEntity.medFhirId == medFhirId
        }
    }
}