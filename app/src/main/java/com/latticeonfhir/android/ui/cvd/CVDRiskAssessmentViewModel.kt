package com.latticeonfhir.android.ui.cvd

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CVDRiskAssessmentViewModel@Inject constructor(

): ViewModel() {
    var isLaunched by mutableStateOf(false)
    val tabs = listOf("Assess risk", "Records")
    var patient by mutableStateOf<PatientResponse?>(null)
    var isDiabetic by mutableStateOf("")
    var isSmoker by mutableStateOf("")
    var systolic by mutableStateOf("")
    var diastolic by mutableStateOf("")
    var bpUnit = "mmHg"
    var cholesterol by mutableStateOf("")
    var selectedCholesterolIndex by mutableIntStateOf(0)
    var cholesterolUnits = listOf("mmol/L", "mg/dl")
    var heightUnits = listOf("cm", "ft/inch")
    var selectedHeightUnitIndex by mutableIntStateOf(0)
    var heightInCM by mutableStateOf("")
    var heightInFeet by mutableStateOf("")
    var heightInInch by mutableStateOf("")
    var weight by mutableStateOf("")
    var weightUnit = "kg"
    var bmi by mutableStateOf("")
    var riskPercentage by mutableStateOf("")
}