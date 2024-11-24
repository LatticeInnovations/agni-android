package com.latticeonfhir.android.ui.cvd

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.YesNoEnum
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.cvd.chart.RiskPredictionChartRepository
import com.latticeonfhir.android.data.local.repository.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CVDRiskAssessmentViewModel @Inject constructor(
    private val riskPredictionChartRepository: RiskPredictionChartRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository
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

    var previousRecords by mutableStateOf(listOf<CVDResponse>())
    var selectedRecord by mutableStateOf<CVDResponse?>(null)

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

    internal fun ifFormValid(): Boolean {
        return isDiabetic.isNotBlank() && isSmoker.isNotBlank()
                && diastolic.isNotBlank() && !diastolicError
                && systolic.isNotBlank() && !systolicError
                && ((cholesterol.isNotBlank() && !cholesterolError)
                || ((heightInCM.isNotBlank() || (heightInFeet.isNotBlank() && heightInInch.isNotBlank()))
                && weight.isNotBlank()
                && !heightInCMError && !heightInFeetError && !heightInInchError
                && !weightError))
    }

    internal fun getRisk() {
        viewModelScope.launch(Dispatchers.IO) {
            var cholesterolInMMHG: Double? = null
            if (cholesterol.isNotBlank()) {
                if (selectedCholesterolIndex == 1) cholesterolInMMHG =
                    cholesterol.toDouble() * 0.0259
                else cholesterolInMMHG = cholesterol.toDouble()
            }
            riskPercentage = riskPredictionChartRepository.getRiskLevels(
                age = patient!!.birthDate.toTimeInMilli().toAge(),
                cholesterol = cholesterolInMMHG,
                diabetes = YesNoEnum.codeFromDisplay(isDiabetic),
                tobaccoStatus = YesNoEnum.codeFromDisplay(isSmoker),
                sex = patient!!.gender[0].uppercaseChar().toString(),
                sys = systolic.toInt(),
                bmi = if (bmi.isNotBlank()) bmi.toDouble() else null
            )
        }
    }

    internal fun getRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            previousRecords = cvdAssessmentRepository.getCVDRecord(patient!!.id)
        }
    }

    internal fun saveCVDRecord(saved: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(
                    Date().toTodayStartDate(),
                    Date().toEndOfDay()
                ).firstOrNull { appointmentEntity ->
                    appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                }
            val cvdResponse = CVDResponse(
                cvdUuid = UUIDBuilder.generateUUID(),
                cvdFhirId = null,
                appointmentId = appointmentResponseLocal!!.appointmentId
                    ?: appointmentResponseLocal.uuid,
                patientId = patient!!.fhirId ?: patient!!.id,
                createdOn = Date(),
                diabetic = YesNoEnum.codeFromDisplay(isDiabetic),
                smoker = YesNoEnum.codeFromDisplay(isSmoker),
                bpDiastolic = diastolic.toInt(),
                bpSystolic = systolic.toInt(),
                cholesterol = if (cholesterol.isNotBlank()) cholesterol.toDouble() else null,
                cholesterolUnit = if (cholesterol.isNotBlank()) cholesterolUnits[selectedCholesterolIndex] else null,
                heightCm = if (selectedHeightUnitIndex == 0) heightInCM.toDouble() else null,
                heightInch = if (selectedHeightUnitIndex == 1) heightInInch.toDouble() else null,
                heightFt = if (selectedHeightUnitIndex == 1) heightInFeet.toInt() else null,
                weight = if (weight.isNotBlank()) weight.toDouble() else null,
                risk = riskPercentage.toInt(),
                practitionerName = preferenceRepository.getUserName(),
                bmi = if (bmi.isNotBlank()) bmi.toDouble() else null
            )
            cvdAssessmentRepository.insertCVDRecord(
                cvdResponse.copy(
                    appointmentId = appointmentResponseLocal.uuid,
                    patientId = patient!!.id
                )
            )
            genericRepository.insertCVDRecord(cvdResponse)
            clearForm()
            saved()
        }
    }

    private fun clearForm() {
        isDiabetic = ""
        isSmoker = ""
        systolic = ""
        diastolic = ""
        cholesterol = ""
        selectedCholesterolIndex = 0
        heightInCM = ""
        heightInFeet = ""
        heightInInch = ""
        selectedHeightUnitIndex = 0
        weight = ""
        riskPercentage = ""
        bmi = ""
    }
}