package com.latticeonfhir.android.ui.cvd

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
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    val tabs = listOf("Records", "Assess risk")
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

    var map = mapOf<String, Any>()

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)

    internal fun getAppointmentInfo(
        callback: () -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patient!!.id,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay() && appointmentResponse.slot.start.time > Date().toTodayStartDate()
            }
            appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                canAddAssessment =
                    appointmentResponse?.status == AppointmentStatusEnum.ARRIVED.value || appointmentResponse?.status == AppointmentStatusEnum.WALK_IN.value
                            || appointmentResponse?.status == AppointmentStatusEnum.IN_PROGRESS.value
                isAppointmentCompleted =
                    appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
            callback()
        }
    }

    internal fun getBmi() {
        if ((heightInCM.isNotBlank() || (heightInFeet.isNotBlank()))
            && weight.isNotBlank()
            && !heightInCMError && !heightInFeetError && !heightInInchError
            && !weightError
        ) {
            val heightInM: Double = if (selectedHeightUnitIndex == 0) heightInCM.toDouble() * 0.01
            else {
                ((heightInFeet.toDouble() * 12) + heightInInch.ifBlank { "0" }.toDouble()) * 0.0254
            }
            bmi = (weight.toDouble() / (heightInM * heightInM)).toInt().toString()
        } else bmi = ""
    }

    internal fun ifFormValid(): Boolean {
        return isDiabetic.isNotBlank() && isSmoker.isNotBlank()
                && diastolic.isNotBlank() && !diastolicError
                && systolic.isNotBlank() && !systolicError
                && (cholesterol.isNotBlank()
                || ((heightInCM.isNotBlank() || heightInFeet.isNotBlank())
                && weight.isNotBlank()))
                && !heightInCMError && !heightInFeetError && !heightInInchError
                && !weightError  && !cholesterolError
    }

    internal fun getTodayCVDAssessment(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            cvdAssessmentRepository.getCVDRecord(patient!!.id).firstOrNull()?.let { record ->
                    isDiabetic = YesNoEnum.displayFromCode(record.diabetic)
                    isSmoker = YesNoEnum.displayFromCode(record.smoker)
                }
        }
    }
    internal fun getRisk(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            var cholesterolInMMHG: Double? = null
            if (cholesterol.isNotBlank()) {
                cholesterolInMMHG =
                    if (selectedCholesterolIndex == 1) cholesterol.toDouble() * 0.0259
                    else cholesterol.toDouble()
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

    internal fun getRecords(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
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

    internal fun saveCVDRecord(
        saved: () -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()
            val cvdResponse = getCVDRecord(practitionerName = preferenceRepository.getUserName())
            cvdAssessmentRepository.insertCVDRecord(
                cvdResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id
                )
            )
            appointmentRepository.updateAppointment(
                appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                    .also { updatedAppointmentResponse ->
                        appointmentResponseLocal = updatedAppointmentResponse
                    }
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


    internal fun addPatientToQueue(
        patient: PatientResponse,
        addedToQueue: (List<Long>) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.addPatientToQueue(
                patient,
                scheduleRepository,
                genericRepository,
                preferenceRepository,
                appointmentRepository,
                patientLastUpdatedRepository,
                addedToQueue
            )
        }
    }

    internal fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.updateStatusToArrived(
                patient,
                appointment,
                appointmentRepository,
                genericRepository,
                preferenceRepository,
                scheduleRepository,
                patientLastUpdatedRepository,
                updated
            )
        }
    }
}