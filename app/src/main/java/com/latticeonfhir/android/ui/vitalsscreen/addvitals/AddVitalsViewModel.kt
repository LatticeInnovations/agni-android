package com.latticeonfhir.android.ui.vitalsscreen.addvitals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.repository.vital.VitalRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.vitalsscreen.enums.BGEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.EyeTestTypeEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.TemperatureEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.VitalsEyeEnum
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.android.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.constants.VitalConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddVitalsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val vitalRepository: VitalRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var vitalLocal by mutableStateOf<VitalLocal?>(null)
    var isShowLeftEyeSheet by mutableStateOf(false)
    var isShowRightEyeSheet by mutableStateOf(false)
    var heightType by mutableStateOf("ft/in")
    var feet by mutableStateOf("")
    var isFeetNotValid by mutableStateOf(false)
    var inch by mutableStateOf("")
    var isInchNotValid by mutableStateOf(false)
    var centimeter by mutableStateOf("")
    var isCmNotValid by mutableStateOf(false)
    var weight by mutableStateOf("")
    var isWeightNotValid by mutableStateOf(false)
    var withGlassChipSelected by mutableStateOf(false)
    var withoutGlassChipSelected by mutableStateOf(true)
    var leftEye by mutableStateOf("")
    var rightEye by mutableStateOf("")
    var heartRate by mutableStateOf("")
    var isHeartNotValid by mutableStateOf(false)
    var respiratoryRate by mutableStateOf("")
    var isRespNotValid by mutableStateOf(false)
    var spo2 by mutableStateOf("")
    var isSpo2NotValid by mutableStateOf(false)
    var temperature by mutableStateOf("")
    var isTempNotValid by mutableStateOf(false)
    var temperatureType by mutableStateOf("Fahrenheit")
    var bpDiastolic by mutableStateOf("")
    var isDiastolicNotValid by mutableStateOf(false)
    var bpSystolic by mutableStateOf("")
    var isSystolicNotValid by mutableStateOf(false)
    var bloodGlucose by mutableStateOf("")
    var isBgNotValid by mutableStateOf(false)
    var bgRandomChipSelected by mutableStateOf(true)
    var bgFastingChipSelected by mutableStateOf(false)
    var bgType by mutableStateOf("mg/dl")
    internal var appointmentResponseLocal: AppointmentResponseLocal? = null
    var map = mapOf<String, Any>()
    var isButtonClicked by mutableStateOf(false)

    var cholesterol by mutableStateOf("")
    var cholesterolError by mutableStateOf(false)
    var selectedCholesterolIndex by mutableIntStateOf(0)
    var cholesterolUnits = listOf("mmol/L", "mg/dl")

    fun validateVitalsDetails(): Boolean {
        return !(checkHeight() || checkWeight() || checkHearRate() || checkRR() || checkSpo2() || checkTemp() || checkBG() || checkBP() || checkCholesterol() || checkEyes() || checkAllFields())
    }

    private fun checkAllFields(): Boolean {
        return (feet.isBlank() && inch.isBlank() && centimeter.isBlank() && weight.isBlank() && heartRate.isBlank() && respiratoryRate.isBlank() && spo2.isBlank() && temperature.isBlank() && bpSystolic.isBlank() && bpDiastolic.isBlank() && bloodGlucose.isBlank() && leftEye.isBlank() && rightEye.isBlank() && cholesterol.isBlank())

    }

    private fun checkEyes(): Boolean {
        return ((leftEye.isNotBlank() || rightEye.isNotBlank()) && (isFeetNotValid || isWeightNotValid || isHeartNotValid || isRespNotValid || isSpo2NotValid || isTempNotValid || isSystolicNotValid || isDiastolicNotValid || isBgNotValid || cholesterolError))

    }

    private fun checkHeight(): Boolean {
        return ((feet.isNotBlank() && isFeetNotValid) || (inch.isNotBlank() && isInchNotValid) || (centimeter.isNotBlank() && isCmNotValid))

    }

    private fun checkCholesterol(): Boolean {
        return (cholesterol.isNotBlank() && cholesterolError)
    }

    private fun checkBP(): Boolean {
        return ((bpSystolic.isNotBlank() || bpDiastolic.isNotBlank()) && (bpSystolic.isBlank() || bpDiastolic.isBlank() || isSystolicNotValid || isDiastolicNotValid))

    }

    private fun checkBG(): Boolean {
        return (bloodGlucose.isNotBlank() && isBgNotValid)

    }

    private fun checkTemp(): Boolean {
        return (temperature.isNotBlank() && isTempNotValid)

    }

    private fun checkSpo2(): Boolean {
        return (spo2.isNotBlank() && isSpo2NotValid)

    }

    private fun checkRR(): Boolean {
        return (respiratoryRate.isNotBlank() && isRespNotValid)

    }

    private fun checkHearRate(): Boolean {
        return (heartRate.isNotBlank() && isHeartNotValid)

    }

    private fun checkWeight(): Boolean {
        return (weight.isNotBlank() && isWeightNotValid)
    }


    internal suspend fun getStudentTodayAppointment(
        startDate: Date, endDate: Date, patientId: String
    ) {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
                .firstOrNull { appointmentEntity ->
                    appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                }
    }


    internal fun insertVital(
        vitalUuid: String = UUID.randomUUID().toString(),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO, inserted: (Long) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            // if there is no appointment, create appointment with walk in status
            if (appointmentResponseLocal == null) {
                Queries.addPatientToQueue(
                    patient!!,
                    scheduleRepository,
                    genericRepository,
                    preferenceRepository,
                    appointmentRepository,
                    patientLastUpdatedRepository
                ) {
                    viewModelScope.launch(ioDispatcher) {
                        getStudentTodayAppointment(
                            Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                        )
                        createVital(
                            vitalUuid, ioDispatcher, inserted
                        )

                    }
                }
            } else {
                createVital(
                    vitalUuid, ioDispatcher, inserted
                )
            }
        }
    }

    internal fun updateVital(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO, updated: (Int) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            // if there is no appointment, create appointment with walk in status
            if (appointmentResponseLocal != null) {
                insertUpdateVital(ioDispatcher, updated)
            }
        }
    }

    private suspend fun createVital(
        vitalUuid: String,
        ioDispatcher: CoroutineDispatcher, inserted: (Long) -> Unit
    ) {

        inserted(withContext(ioDispatcher) {
            val generatedOn = Date()
            insertVitalInDB(
                getVitalDetails(
                    vitalUuid, null, preferenceRepository.getUserName(), generatedOn
                )
            ).also {
                insertGenericEntityInDB(
                    getVitalDetails(
                        vitalUuid, null, null, generatedOn
                    )
                )
                checkAndUpdateAppointmentStatusToInProgress(
                    inProgressTime = generatedOn,
                    patient = patient!!,
                    appointmentResponseLocal = appointmentResponseLocal!!,
                    appointmentRepository = appointmentRepository,
                    scheduleRepository = scheduleRepository,
                    genericRepository = genericRepository
                )
                updatePatientLastUpdated(
                    patient!!.id,
                    patientLastUpdatedRepository,
                    genericRepository
                )
            }
        })
    }

    private suspend fun insertUpdateVital(
        ioDispatcher: CoroutineDispatcher, updated: (Int) -> Unit
    ) {

        viewModelScope.launch(ioDispatcher) {
            updateVitalInDB(
                getVitalDetails(
                    vitalUuid = vitalLocal!!.vitalUuid,
                    fhirId = vitalLocal?.fhirId,
                    practitionerName = preferenceRepository.getUserName(),
                    createdOn = vitalLocal!!.createdOn
                )
            ).also {

                genericRepository.insertVital(
                        getVitalDetails(
                            vitalUuid = vitalLocal!!.vitalUuid,
                            fhirId = vitalLocal?.fhirId,
                            practitionerName = preferenceRepository.getUserName(),
                            createdOn = vitalLocal!!.createdOn
                        )
                    )

                updatePatientLastUpdated(
                    patient!!.id,
                    patientLastUpdatedRepository,
                    genericRepository
                )

                updated(it)
            }
        }
    }

    fun checkIsEdit(): Boolean {
        return if (vitalLocal != null) {
            vitalLocal != getVitalDetails(
                vitalUuid = vitalLocal!!.vitalUuid,
                fhirId = vitalLocal?.fhirId,
                practitionerName = preferenceRepository.getUserName(),
                createdOn = vitalLocal!!.createdOn
            )
        } else false
    }


    private suspend fun insertVitalInDB(
        vitalLocal: VitalLocal
    ): Long {
        return vitalRepository.insertVital(
            vitalLocal = vitalLocal
        )
    }

    private suspend fun insertGenericEntityInDB(
        vitalLocal: VitalLocal
    ): Long {
        return genericRepository.insertVital(
            vitalLocal
        )
    }

    private suspend fun updateVitalInDB(
        vitalLocal: VitalLocal
    ): Int {
        return vitalRepository.updateVital(
            vitalLocal
        )
    }

    private fun String.getEyeTypeNumber(): Int {
        return when (this) {
            VitalsEyeEnum.NORMAL_VISION.value -> VitalsEyeEnum.NORMAL_VISION.number
            VitalsEyeEnum.MILD_IMPAIRMENT.value -> VitalsEyeEnum.MILD_IMPAIRMENT.number
            VitalsEyeEnum.MODERATE_IMPAIRMENT.value -> VitalsEyeEnum.MODERATE_IMPAIRMENT.number
            VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.value -> VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.number
            VitalsEyeEnum.SEVERE_IMPAIRMENT.value -> VitalsEyeEnum.SEVERE_IMPAIRMENT.number
            VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.value -> VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.number
            VitalsEyeEnum.LEGAL_BLINDNESS.value -> VitalsEyeEnum.LEGAL_BLINDNESS.number
            else -> {
                0
            }
        }
    }

    private fun Int.getEyeTypeName(): String {
        return when (this) {
            VitalsEyeEnum.NORMAL_VISION.number -> VitalsEyeEnum.NORMAL_VISION.value
            VitalsEyeEnum.MILD_IMPAIRMENT.number -> VitalsEyeEnum.MILD_IMPAIRMENT.value
            VitalsEyeEnum.MODERATE_IMPAIRMENT.number -> VitalsEyeEnum.MODERATE_IMPAIRMENT.value
            VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.number -> VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.value
            VitalsEyeEnum.SEVERE_IMPAIRMENT.number -> VitalsEyeEnum.SEVERE_IMPAIRMENT.value
            VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.number -> VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.value
            VitalsEyeEnum.LEGAL_BLINDNESS.number -> VitalsEyeEnum.LEGAL_BLINDNESS.value
            else -> {
                ""
            }
        }
    }


    private fun getVitalDetails(
        vitalUuid: String, fhirId: String?, practitionerName: String?, createdOn: Date
    ): VitalLocal {
        return VitalLocal(
            vitalUuid = vitalUuid,
            fhirId = fhirId,
            patientId = patient!!.id,
            appointmentId = appointmentResponseLocal!!.uuid,
            bloodGlucose = if (bgType == BGEnum.BG_MMO.value) bloodGlucose.trim()
                .addZeroAfterDot() else bloodGlucose.ifBlank { null },
            bloodGlucoseType = getGlucoseType(),
            bloodGlucoseUnit = if (bloodGlucose.isNotBlank()) bgType else null,
            bpDiastolic = bpDiastolic.ifBlank { null },
            bpSystolic = bpSystolic.ifBlank { null },
            createdOn = createdOn,
            heartRate = heartRate.ifBlank { null },
            heightCm = centimeter.addZeroAfterDot().ifBlank { null },
            heightFt = feet.addZeroAfterDot().ifBlank { null },
            heightInch = if (feet.isNotBlank() && inch.isBlank()) "0" else inch.addZeroAfterDot()
                .ifBlank { null },
            eyeTestType = getEyeTestType(),
            leftEye = if (leftEye.isNotBlank()) leftEye.getEyeTypeNumber() else null,
            rightEye = if (rightEye.isNotBlank()) rightEye.getEyeTypeNumber() else null,
            respRate = respiratoryRate.ifBlank { null },
            spo2 = spo2.ifBlank { null },
            temp = temperature.addZeroAfterDot().ifBlank { null },
            tempUnit = if (temperature.isNotBlank() && temperatureType.lowercase() == TemperatureEnum.FAHRENHEIT.name.lowercase()) TemperatureEnum.FAHRENHEIT.value else if (temperature.isNotBlank()) TemperatureEnum.CELSIUS.value else null,
            weight = weight.addZeroAfterDot().ifBlank { null },
            practitionerName = practitionerName,
            cholesterol = if (cholesterol.isNotBlank()) cholesterol.addZeroAfterDot()
                .toDouble() else null,
            cholesterolUnit = if (cholesterol.isNotBlank()) cholesterolUnits[selectedCholesterolIndex] else null,
        )
    }

    private fun getEyeTestType(): String? {
        return if ((leftEye.isNotBlank() || rightEye.isNotBlank()) && withoutGlassChipSelected) EyeTestTypeEnum.WITHOUT_GLASSES.value else if ((leftEye.isNotBlank() || rightEye.isNotBlank()) && withGlassChipSelected) EyeTestTypeEnum.WITH_GLASSES.value else null
    }

    private fun getGlucoseType(): String? {
        return if (bloodGlucose.isNotBlank() && bgRandomChipSelected) BGEnum.RANDOM.value else if (bloodGlucose.isNotBlank() && bgFastingChipSelected) BGEnum.FASTING.value else null
    }

    internal fun setVitalDetails() {
        vitalLocal?.let {
            feet = it.heightFt.emptyStringIfNull()
            inch = it.heightInch.emptyStringIfNull()
            centimeter = it.heightCm.emptyStringIfNull()
            heightType =
                if (!it.heightCm.isNullOrBlank()) VitalConstants.HEIGHT_CENTIMETER else VitalConstants.HEIGHT_IN_FT_IN
            weight = it.weight.emptyStringIfNull()
            if (it.eyeTestType.emptyStringIfNull() == EyeTestTypeEnum.WITH_GLASSES.value) {
                withGlassChipSelected = true
                withoutGlassChipSelected = false
            } else withoutGlassChipSelected = true
            leftEye = it.leftEye.zeroIntIfNull().getEyeTypeName()
            rightEye = it.rightEye.zeroIntIfNull().getEyeTypeName()
            heartRate = it.heartRate.emptyStringIfNull()
            respiratoryRate = it.respRate.emptyStringIfNull()
            spo2 = it.spo2.emptyStringIfNull()
            temperature = it.temp.emptyStringIfNull()
            temperatureType =
                if (it.tempUnit.emptyStringIfNull() == TemperatureEnum.CELSIUS.value) TemperatureEnum.CELSIUS.name.capitalizeFirstLetter()
                else TemperatureEnum.FAHRENHEIT.name.capitalizeFirstLetter()
            bpSystolic = it.bpSystolic.emptyStringIfNull()
            bpDiastolic = it.bpDiastolic.emptyStringIfNull()
            bloodGlucose = it.bloodGlucose.emptyStringIfNull()
            if (it.bloodGlucoseType.emptyStringIfNull() == BGEnum.FASTING.value) {
                bgFastingChipSelected = true
                bgRandomChipSelected = false
            } else bgRandomChipSelected = true
            bgType =
                if (it.bloodGlucoseUnit == BGEnum.BG_MMO.value) BGEnum.BG_MMO.value else BGEnum.BG_MG.value
            selectedCholesterolIndex = if (it.cholesterolUnit != null)
                cholesterolUnits.indexOf(it.cholesterolUnit)
            else 0
            cholesterol = if (selectedCholesterolIndex == 0)
                it.cholesterol?.toString() ?: ""
            else it.cholesterol?.toInt()?.toString() ?: ""
        }
    }

    private fun String?.emptyStringIfNull(): String {
        return this ?: ""
    }

    private fun Int?.zeroIntIfNull(): Int {
        return this ?: 0
    }

    private fun String.capitalizeFirstLetter(): String {
        return this.lowercase().replaceFirstChar { it.uppercaseChar() }
    }


    private fun String.addZeroAfterDot(): String {
        return if (this.isNotBlank() && this.endsWith(".")) {
            "${this}0"
        } else {
            this
        }
    }

}