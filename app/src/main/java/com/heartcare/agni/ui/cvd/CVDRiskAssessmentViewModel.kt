package com.heartcare.agni.ui.cvd

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.AppointmentTypeEnum
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.config.RiskConfig
import com.heartcare.agni.data.local.model.config.RiskItem
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.config.RemoteConfigRepository
import com.heartcare.agni.data.local.repository.cvd.chart.RiskPredictionChartRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.referral.ReferralRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.loadAppointmentInfo
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.plusMinusDays
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentEndTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toScheduleEndTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toScheduleStartTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val referralRepository: ReferralRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    val tabs = listOf("Assess risk", "Records")
    val maxChiefComplaintLength = 200
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
    var weightUnits = listOf("kg", "lb")
    var selectedWeightUnitIndex by mutableIntStateOf(0)
    var weight by mutableStateOf("")
    var weightError by mutableStateOf(false)
    var bmi by mutableStateOf("")
    var riskPercentage by mutableStateOf("")

    var previousRecordsWithReferralStatus by mutableStateOf(listOf<Pair<CVDResponse, Boolean>>())
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
    var existsInOtherHospital by mutableStateOf(false)

    var screeningDate by mutableStateOf(Date())
    var showDatePicker by mutableStateOf(false)
    var chiefComplaint by mutableStateOf("")
    var previousHeartAttack by mutableStateOf("")

    var todayCVD by mutableStateOf<CVDResponse?>(null)

    private val riskConfig = MutableStateFlow<RiskConfig?>(null)
    var followUpDate: Date? by mutableStateOf(null)
    var showFollowUpDialog by mutableStateOf(false)
    var isReferralAlreadyExists by mutableStateOf(false)

    init {
        viewModelScope.launch(ioDispatcher) {
            riskConfig.value = remoteConfigRepository.getRiskConfig()
        }
    }

    fun getAppointmentInfo(
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val info = loadAppointmentInfo(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                maxNumberOfAppointmentsInADay = maxNumberOfAppointmentsInADay,
                appointmentRepository = appointmentRepository
            )
            appointment = info.appointment
            existsInOtherHospital = info.existsInOtherHospital
            canAddAssessment = info.canAddAssessment
            isAppointmentCompleted = info.isAppointmentCompleted
            ifAllSlotsBooked = info.ifAllSlotsBooked
            callback()
        }
    }

    fun getBmi() {
        if ((heightInCM.isNotBlank() || heightInFeet.isNotBlank() || heightInInch.isNotBlank())
            && weight.isNotBlank()
            && !heightInCMError && !heightInFeetError && !heightInInchError
            && !weightError
        ) {
            val heightInM: Double = if (selectedHeightUnitIndex == 0) {
                heightInCM.toDouble() * 0.01
            } else {
                ((heightInFeet.toDouble() * 12) + heightInInch.ifBlank { "0" }.toDouble()) * 0.0254
            }

            val weightInKg: Double = if (selectedWeightUnitIndex == 0) {
                weight.toDouble()
            } else {
                weight.toDouble() * 0.453592 // 1 lb = 0.453592 kg
            }

            bmi = "%.1f".format(weightInKg / (heightInM * heightInM))
        } else {
            bmi = ""
        }
    }

    fun ifFormValid(): Boolean {
        return isDiabetic.isNotBlank() &&
                isSmoker.isNotBlank() &&
                previousHeartAttack.isNotBlank() &&
                diastolic.isNotBlank() && !diastolicError &&
                systolic.isNotBlank() && !systolicError &&
                (heightInCM.isNotBlank() || (heightInFeet.isNotBlank() && heightInInch.isNotBlank())) &&
                weight.isNotBlank() &&
                !heightInCMError && !heightInFeetError && !heightInInchError &&
                !weightError && !cholesterolError
    }

    fun getTodayCVDAssessment(onLoaded: () -> Unit = {}) {
        viewModelScope.launch(ioDispatcher) {
            getRecords()
            previousRecordsWithReferralStatus.map { it.first }.firstOrNull()?.let { record ->
                isDiabetic = YesNoEnum.displayFromCode(record.diabetic)
                isSmoker = YesNoEnum.displayFromCode(record.smoker)
                previousHeartAttack = YesNoEnum.displayFromCode(record.heartAttackHistory)
                weight = record.weight.toString()
                selectedWeightUnitIndex = weightUnits.indexOf(record.weightUnit)
                if (record.heightCm != null) {
                    heightInCM = record.heightCm.toString()
                    selectedHeightUnitIndex = 0
                } else {
                    heightInFeet = record.heightFt?.toString() ?: ""
                    heightInInch = record.heightInch?.toString() ?: ""
                    selectedHeightUnitIndex = 1
                }
                getBmi()
                if (record.createdOn.time in Date().toTodayStartDate()..Date().toEndOfDay()) {
                    todayCVD = record
                    screeningDate = record.screeningDate
                    chiefComplaint = record.chiefComplaint ?: ""
                    systolic = record.bpSystolic.toString()
                    diastolic = record.bpDiastolic.toString()
                    cholesterol = record.cholesterol?.toString() ?: ""
                    selectedCholesterolIndex =
                        if (record.cholesterolUnit.isNullOrBlank()) 0 else cholesterolUnits.indexOf(
                            record.cholesterolUnit
                        )
                }
            }
            onLoaded()
        }
    }

    fun getRisk(
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

    private suspend fun getRecords() {
        val appointmentIds =
            getInProgressCompletedAppointmentIds(patient!!.id, appointmentRepository)
        previousRecordsWithReferralStatus =
            cvdAssessmentRepository.getCVDRecordByAppointmentIds(*appointmentIds.toTypedArray())
                .map { cvdResponse ->
                    Pair(
                        cvdResponse,
                        checkIfReferralExists(
                            cvdResponse.appointmentId
                        )
                    )
                }
    }

    private fun getCVDRecord(
        cvdUUid: String = UUIDBuilder.generateUUID(),
        cvdFhirId: String? = null,
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
            weight = weight.toDouble(),
            risk = riskPercentage.toInt(),
            bmi = bmi.toDouble(),
            appUpdatedDate = Date(),
            weightUnit = weightUnits[selectedWeightUnitIndex],
            chiefComplaint = chiefComplaint.trim().ifBlank { null },
            screeningDate = screeningDate,
            heartAttackHistory = YesNoEnum.codeFromDisplay(previousHeartAttack),
            practitionerName = null
        )
    }

    fun saveCVDRecord(
        saved: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )
            val cvdResponse = getCVDRecord(
                cvdUUid = todayCVD?.cvdUuid ?: UUIDBuilder.generateUUID()
            )
            cvdAssessmentRepository.insertCVDRecord(
                cvdResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        preferenceRepository.getUserDetails()!!.firstName,
                        preferenceRepository.getUserDetails()!!.lastName
                    )
                )
            )
            genericRepository.insertCVDRecord(cvdResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = cvdResponse.createdOn,
                patient = patient!!,
                appointmentResponseLocal = appointmentResponseLocal!!,
                appointmentRepository = appointmentRepository,
                scheduleRepository = scheduleRepository,
                genericRepository = genericRepository,
                preferenceRepository = preferenceRepository
            )
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )
            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
            val appointmentDate =
                cvdResponse.createdOn.plusMinusDays(getRiskItem(riskPercentage.toInt()).appointmentDays)
            isReferralAlreadyExists = checkIfReferralExists(appointmentResponseLocal!!.uuid)
            followUpDate = createFollowUpAppointment(date = appointmentDate)
            saved()
        }
    }

    fun clearForm() {
        screeningDate = Date()
        chiefComplaint = ""
        isDiabetic = ""
        isSmoker = ""
        previousHeartAttack = ""
        systolic = ""
        diastolic = ""
        cholesterol = ""
        selectedCholesterolIndex = 0
        heightInCM = ""
        heightInFeet = ""
        heightInInch = ""
        selectedHeightUnitIndex = 0
        weight = ""
        selectedWeightUnitIndex = 0
        riskPercentage = ""
        bmi = ""
        followUpDate = null
    }


    fun addPatientToQueue(
        patient: PatientResponse,
        addedToQueue: (List<Long>) -> Unit
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

    fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.updateStatusToArrived(
                patient,
                appointment,
                appointmentRepository,
                genericRepository,
                scheduleRepository,
                preferenceRepository,
                patientLastUpdatedRepository,
                updated
            )
        }
    }

    fun checkIfCVDExistsForScreenDate(
        exists: (Boolean) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            exists(
                screeningDate.toTodayStartDate() != todayCVD?.screeningDate?.toTodayStartDate() &&
                        cvdAssessmentRepository.getCVDRecordByScreeningDate(
                            patient!!.id,
                            screeningDate.toTodayStartDate(),
                            screeningDate.toEndOfDay()
                        ) != null
            )
        }
    }

    fun getRiskItem(riskPercentage: Int): RiskItem {
        val config = riskConfig.value!!
        return when {
            riskPercentage < 10 -> config.lt10
            riskPercentage in 10..19 -> config.range10to19
            riskPercentage in 20..29 -> config.range20to29
            else -> config.gte30
        }
    }

    private suspend fun createFollowUpAppointment(
        date: Date
    ): Date {
        appointmentRepository.getAppointmentsOfPatientByDate(
            patient!!.id,
            date.toTodayStartDate(),
            date.toEndOfDay()
        ).let { existingAppointment ->
            return if (existingAppointment == null) {
                // create appointment
                createNewAppointment(date)
            } else {
                // appointment already exists
                if (existingAppointment.hospitalCode == user.hospitalCode) {
                    (existingAppointment.slot.start)
                } else {
                    // appointment exists in other facility
                    // create appointment for next date
                    createFollowUpAppointment(
                        date = date.plusMinusDays(1)
                    )
                }
            }
        }
    }

    private suspend fun createNewAppointment(
        date: Date
    ): Date {
        var id = UUIDBuilder.generateUUID()
        var scheduleFhirId: String? = null
        var scheduleId = date.toScheduleStartTime().time
        scheduleRepository.getScheduleByStartTime(
            scheduleId,
            user.hospitalCode
        ).let { scheduleResponse ->
            if (scheduleResponse != null) {
                id = scheduleResponse.uuid
                scheduleFhirId = scheduleResponse.scheduleId
                scheduleId = scheduleResponse.planningHorizon.start.time
                updateSchedule(scheduleResponse)
            } else {
                createNewSchedule(id, date)
            }
        }.also {
            val appointmentId = UUIDBuilder.generateUUID()
            val createdOn = Date()
            val appointmentStartTime = date.toScheduleStartTime()
            val appointmentEndTime = appointmentStartTime.toAppointmentEndTime()
            val slot = Slot(
                start = appointmentStartTime,
                end = appointmentEndTime
            )
            appointmentRepository.addAppointment(
                AppointmentResponseLocal(
                    appointmentId = null,
                    uuid = appointmentId,
                    patientId = patient?.id!!,
                    scheduleId = Date(scheduleId),
                    createdOn = createdOn,
                    slot = slot,
                    status = AppointmentStatusEnum.SCHEDULED.value,
                    appointmentType = AppointmentTypeEnum.ROUTINE.code,
                    inProgressTime = null,
                    roleId = user.accountGroupId.toString(),
                    slotId = null,
                    practitionerId = user.fhirId,
                    hospitalFhirId = null,
                    hospitalId = user.hospitalId.toString(),
                    hospitalName = user.hospitalName,
                    hospitalCode = user.hospitalCode
                )
            ).also {
                genericRepository.insertAppointment(
                    AppointmentResponse(
                        appointmentId = null,
                        uuid = appointmentId,
                        patientFhirId = patient!!.fhirId ?: patient!!.id,
                        scheduleId = scheduleFhirId ?: id,
                        createdOn = createdOn,
                        slot = slot,
                        status = AppointmentStatusEnum.SCHEDULED.value,
                        appointmentType = AppointmentTypeEnum.ROUTINE.code,
                        inProgressTime = null,
                        roleId = null,
                        slotId = null,
                        practitionerId = null,
                        hospitalFhirId = null,
                        hospitalId = null,
                        hospitalName = null,
                        hospitalCode = null,
                        appUpdatedDate = Date()
                    )
                )
                val patientLastUpdatedResponse = PatientLastUpdatedResponse(
                    uuid = patient!!.id,
                    timestamp = Date()
                )
                patientLastUpdatedRepository.insertPatientLastUpdatedData(
                    patientLastUpdatedResponse
                )
                genericRepository.insertPatientLastUpdated(
                    patientLastUpdatedResponse
                )
                return date
            }
        }
    }

    private suspend fun updateSchedule(scheduleResponse: ScheduleResponse) {
        scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots!! + 1
            )
        )
    }

    private suspend fun createNewSchedule(
        id: String,
        date: Date
    ) {
        val scheduleStartTime = date.toScheduleStartTime()
        val scheduleEndTime = scheduleStartTime.toScheduleEndTime()
        val schedule = ScheduleResponse(
            uuid = id,
            scheduleId = null,
            planningHorizon = Slot(
                start = scheduleStartTime,
                end = scheduleEndTime
            ),
            bookedSlots = null,
            roleId = null,
            active = null,
            practitionerId = null,
            hospitalId = null,
            hospitalFhirId = null,
            hospitalName = null,
            hospitalCode = null
        )
        scheduleRepository.insertSchedule(
            schedule.copy(
                bookedSlots = 1,
                roleId = user.accountGroupId.toString(),
                active = true,
                practitionerId = user.fhirId,
                hospitalId = user.hospitalId.toString(),
                hospitalFhirId = null,
                hospitalName = user.hospitalName,
                hospitalCode = user.hospitalCode
            )
        )
        genericRepository.insertSchedule(
            schedule
        )
    }

    private suspend fun checkIfReferralExists(
        appointmentId: String
    ): Boolean {
        val referral = referralRepository.getReferralByAppointmentId(appointmentId)
        return referral != null
    }
}