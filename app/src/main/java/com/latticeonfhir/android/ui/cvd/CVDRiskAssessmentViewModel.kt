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
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
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
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)
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

    var todayAssessment by mutableStateOf<CVDResponse?>(null)
    var map = mapOf<String, Any>()

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

    internal fun getTodayCVDAssessment() {
        viewModelScope.launch(Dispatchers.IO) {
            todayAssessment = cvdAssessmentRepository.getTodayCVDRecord(
                patient!!.id,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            )
            if (todayAssessment == null) {
                cvdAssessmentRepository.getCVDRecord(patient!!.id).firstOrNull()?.let { record ->
                    isDiabetic = YesNoEnum.displayFromCode(record.diabetic)
                    isSmoker = YesNoEnum.displayFromCode(record.smoker)
                }
            } else setData(todayAssessment!!)
        }
    }

    private fun setData(todayAssessment: CVDResponse) {
        isDiabetic = YesNoEnum.displayFromCode(todayAssessment.diabetic)
        isSmoker = YesNoEnum.displayFromCode(todayAssessment.smoker)
        systolic = todayAssessment.bpSystolic.toString()
        diastolic = todayAssessment.bpDiastolic.toString()
        cholesterol = todayAssessment.cholesterol?.toString() ?: ""
        selectedCholesterolIndex = if (todayAssessment.cholesterolUnit != null)
            cholesterolUnits.indexOf(todayAssessment.cholesterolUnit)
        else 0
        heightInCM = todayAssessment.heightCm?.toString() ?: ""
        heightInFeet = todayAssessment.heightFt?.toString() ?: ""
        heightInInch = todayAssessment.heightInch?.toString() ?: ""
        selectedHeightUnitIndex =
            if (heightInFeet.isNotBlank() || heightInInch.isNotBlank()) 1
            else 0
        weight = todayAssessment.weight?.toString() ?: ""
        riskPercentage = todayAssessment.risk.toString()
        getBmi()
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


    private fun getCVDRecord(
        cvdUUid: String = UUIDBuilder.generateUUID(),
        cvdFhirId: String? = null,
        practitionerName: String,
        createdOn: Date = Date()
    ): CVDResponse {
        return CVDResponse(
            cvdUuid = cvdUUid,
            cvdFhirId = cvdFhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            createdOn = createdOn,
            diabetic = YesNoEnum.codeFromDisplay(isDiabetic),
            smoker = YesNoEnum.codeFromDisplay(isSmoker),
            bpDiastolic = diastolic.toInt(),
            bpSystolic = systolic.toInt(),
            cholesterol = if (cholesterol.isNotBlank()) cholesterol.toDouble() else null,
            cholesterolUnit = if (cholesterol.isNotBlank()) cholesterolUnits[selectedCholesterolIndex] else null,
            heightCm = if (selectedHeightUnitIndex == 0 && heightInCM.isNotBlank()) heightInCM.toDouble() else null,
            heightInch = if (selectedHeightUnitIndex == 1 && heightInInch.isNotBlank()) heightInInch.toDouble() else null,
            heightFt = if (selectedHeightUnitIndex == 1 && heightInFeet.isNotBlank()) heightInFeet.toInt() else null,
            weight = if (weight.isNotBlank()) weight.toDouble() else null,
            risk = riskPercentage.toInt(),
            practitionerName = practitionerName,
            bmi = if (bmi.isNotBlank()) bmi.toDouble() else null
        )
    }

    internal fun saveCVDRecord(saved: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getAppointment()
            val cvdResponse = getCVDRecord(practitionerName = preferenceRepository.getUserName())
            cvdAssessmentRepository.insertCVDRecord(
                cvdResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id
                )
            )
            genericRepository.insertCVDRecord(cvdResponse)
            clearForm()
            getTodayCVDAssessment()
            saved()
        }
    }

    private suspend fun getAppointment() {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
            }
    }

    internal fun updateCVDRecord(updated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getAppointment()
            val cvdResponse = getCVDRecord(
                cvdUUid = todayAssessment!!.cvdUuid,
                cvdFhirId = todayAssessment!!.cvdFhirId,
                practitionerName = preferenceRepository.getUserName(),
                createdOn = todayAssessment!!.createdOn
            )
            cvdAssessmentRepository.updateCVDRecord(
                cvdResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id
                )
            ).also {
                if (todayAssessment!!.cvdFhirId != null) {
                    checkAndUpdateVitals(cvdResponse)
                } else {
                    genericRepository.insertCVDRecord(cvdResponse)
                }
                clearForm()
                getTodayCVDAssessment()
                updated()
            }
        }
    }

    private suspend fun checkAndUpdateVitals(
        cvdResponse: CVDResponse
    ) {
        // diabetic status
        if (isDiabetic != YesNoEnum.displayFromCode(cvdResponse.diabetic)) {
            addPatch(
                key = DIABETIC_KEY, component = mapOf(
                    OPERATION to REPLACE,
                    DIABETIC to YesNoEnum.codeFromDisplay(isDiabetic)
                )
            )
        }

        // smoking status
        if (isSmoker != YesNoEnum.displayFromCode(cvdResponse.smoker)) {
            addPatch(
                key = SMOKING_KEY, component = mapOf(
                    OPERATION to REPLACE,
                    SMOKER to YesNoEnum.codeFromDisplay(isSmoker)
                )
            )
        }

        // Blood Pressure
        if (diastolic != cvdResponse.bpDiastolic.toString() || systolic != cvdResponse.bpSystolic.toString()
        ) {
            addPatch(
                key = BP_KEY, component = mapOf(
                    OPERATION to REPLACE,
                    BP_DIASTOLIC to diastolic,
                    BP_SYSTOLIC to systolic
                )
            )
        }

        // cholesterol
        if (cholesterol != (cvdResponse.cholesterol?.toString() ?: "") ||
            (cholesterol.isNotBlank() && (cholesterolUnits[selectedCholesterolIndex] != cvdResponse.cholesterolUnit))
        ) {
            addPatch(
                key = CHOLESTEROL_KEY,
                component = mapOf(
                    OPERATION to if (cvdResponse.cholesterol != null) REPLACE else ADD,
                    CHOLESTEROL to cholesterol.ifBlank { "" },
                    CHOLESTEROL_UNIT to if (cholesterol.isNotBlank()) cholesterolUnits[selectedCholesterolIndex] else ""
                )
            )
        }

        // height (Feet, Inch, Cm)
        if (heightInFeet != (cvdResponse.heightFt?.toString()
                ?: "") || heightInInch != (cvdResponse.heightInch?.toString()
                ?: "") || heightInCM != (cvdResponse.heightCm?.toString() ?: "")
        ) {
            addPatch(
                key = HEIGHT_KEY, component = mapOf(
                    OPERATION to if (cvdResponse.heightCm != null || cvdResponse.heightFt != null || cvdResponse.heightInch != null) REPLACE else ADD,
                    HEIGHT_FT to heightInFeet.addZeroAfterDot().ifBlank { "" },
                    HEIGHT_INCH to heightInInch.addZeroAfterDot().ifBlank { "" },
                    HEIGHT_CM to heightInCM.addZeroAfterDot().ifBlank { "" })
            )
        }

        // weight
        if (weight != (cvdResponse.weight?.toString() ?: "")) {
            addPatch(
                key = WEIGHT_KEY,
                component = mapOf(
                    OPERATION to if (cvdResponse.weight != null) REPLACE else ADD,
                    WEIGHT to weight.addZeroAfterDot().ifBlank { "" })
            )
        }

        // bmi
        if (bmi != (cvdResponse.bmi?.toString() ?: "")) {
            addPatch(
                key = BMI_KEY,
                component = mapOf(
                    OPERATION to if (cvdResponse.bmi != null) REPLACE else ADD,
                    BMI to bmi.ifBlank { "" }
                )
            )
        }

        // risk
        if (riskPercentage != cvdResponse.risk.toString()) {
            addPatch(
                key = RISK_KEY,
                component = mapOf(
                    OPERATION to REPLACE,
                    RISK to riskPercentage.toInt()
                )
            )
        }
    }

    private suspend fun addPatch(key: String, component: Map<String, Any>) {
        val cvdFhirId = todayAssessment!!.cvdFhirId

        map = mapOf(
            CVD_FHIR_ID to cvdFhirId!!, KEY to key, COMPONENT to component

        )
        genericRepository.insertOrUpdateCVDPatch(
            cvdFhirId = cvdFhirId, map = map
        )

    }

    private fun String.addZeroAfterDot(): String {
        return if (this.isNotBlank() && this.endsWith(".")) {
            "${this}0"
        } else {
            this
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

    companion object {
        const val KEY = "key"
        const val COMPONENT = "component"
        const val OPERATION = "operation"

        const val REPLACE = "replace"
        const val ADD = "add"

        const val CVD_FHIR_ID = "cvdFhirId"

        const val HEIGHT_KEY = "Height"
        const val HEIGHT_FT = "heightFt"
        const val HEIGHT_INCH = "heightInch"
        const val HEIGHT_CM = "heightCm"

        const val WEIGHT_KEY = "Weight"
        const val WEIGHT = "weight"

        const val DIABETIC_KEY = "Diabetic status"
        const val DIABETIC = "diabetic"

        const val SMOKING_KEY = "Smoking Status"
        const val SMOKER = "smoker"

        const val BP_KEY = "Blood Pressure"
        const val BP_SYSTOLIC = "bpSystolic"
        const val BP_DIASTOLIC = "bpDiastolic"

        const val CHOLESTEROL_KEY = "Cholesterol"
        const val CHOLESTEROL = "cholesterol"
        const val CHOLESTEROL_UNIT = "cholesterolUnit"

        const val BMI_KEY = "BMI"
        const val BMI = "bmi"

        const val RISK_KEY = "CVD Risk Percentage"
        const val RISK = "risk"
    }
}