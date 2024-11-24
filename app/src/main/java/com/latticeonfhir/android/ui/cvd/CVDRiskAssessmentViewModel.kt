package com.latticeonfhir.android.ui.cvd

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.repository.cvd.chart.RiskPredictionChartRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CVDRiskAssessmentViewModel @Inject constructor(
    private val riskPredictionChartRepository: RiskPredictionChartRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    val tabs = listOf("Assess risk", "Records")
    var patient by mutableStateOf<PatientResponse?>(null)
    var isDiabetic by mutableStateOf("")
    var isSmoker by mutableStateOf("")
    var systolic by mutableStateOf("")
    var systolicError by mutableStateOf(false)
    var diastolic by mutableStateOf("")
    var diastolicError by mutableStateOf(false)
    var bpError by mutableStateOf(false)
    var bpUnit = "mmHg"
    var cholesterol by mutableStateOf("")
    var cholesterolError by mutableStateOf(false)
    var selectedCholesterolIndex by mutableIntStateOf(0)
    var cholesterolUnits = listOf("mmol/L", "mg/dl")
    var heightUnits = listOf("cm", "ft/inch")
    var selectedHeightUnitIndex by mutableIntStateOf(0)
    var heightInCM by mutableStateOf("")
    var heightInCMError by mutableStateOf(false)
    var heightInFeet by mutableStateOf("")
    var heightInFeetError by mutableStateOf(false)
    var heightInInch by mutableStateOf("")
    var heightInInchError by mutableStateOf(false)
    var weight by mutableStateOf("")
    var weightError by mutableStateOf(false)
    var weightUnit = "kg"
    var bmi by mutableStateOf("")
    var riskPercentage by mutableStateOf("")

    var previousRecords by mutableStateOf(listOf<String>(""))
    var showBottomSheet by mutableStateOf(false)

    @SuppressLint("DefaultLocale")
    internal fun getBmi() {
        if ((heightInCM.isNotBlank() || (heightInFeet.isNotBlank() && heightInInch.isNotBlank()))
            && weight.isNotBlank()
            && !heightInCMError && !heightInFeetError && !heightInInchError
            && !weightError
        ) {
            val heightInM: Double
            if (selectedHeightUnitIndex == 0) heightInM = heightInCM.toDouble() * 0.01
            else {
                heightInM = ((heightInFeet.toDouble() * 12) + heightInInch.toDouble()) * 0.0254
            }
            bmi =
                String.format("%.2f", (weight.toDouble() / (heightInM * heightInM)))
        } else bmi = ""
    }

    internal fun getRisk() {
        viewModelScope.launch(Dispatchers.IO) {
            riskPercentage = riskPredictionChartRepository.getRiskLevels(
                age = patient!!.birthDate.toTimeInMilli().toAge(),
                cholesterol = if (cholesterol.isNotBlank()) cholesterol.toDouble() else null,
                diabetes = if (isDiabetic == "Yes") 1 else 0,
                tobaccoStatus = if (isSmoker == "Yes") 1 else 0,
                sex = patient!!.gender[0].uppercaseChar().toString(),
                sys = systolic.toInt(),
                bmi = bmi.toDouble()
            )
        }
    }
}