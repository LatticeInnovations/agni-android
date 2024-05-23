package com.latticeonfhir.android.ui.common.appointmentsfab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppointmentsFabViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseViewModel() {

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var ifAlreadyWaiting by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250

    internal fun initialize(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentRepository.getAppointmentsOfPatientByDate(
                patientId,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                ifAlreadyWaiting = if (appointmentResponse == null) false
                else appointmentResponse.status != AppointmentStatusEnum.SCHEDULED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
            appointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay() && appointmentResponse.slot.start.time > Date().toTodayStartDate()
            }
        }
    }

    internal fun addPatientToQueue(patient: PatientResponse, addedToQueue: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedSlot = Date().toSlotStartTime()
            var scheduleId = Date(
                selectedSlot.toCurrentTimeInMillis(
                    Date()
                )
            )
            var scheduleFhirId: String? = null
            scheduleRepository.getScheduleByStartTime(
                selectedSlot.toCurrentTimeInMillis(
                    Date()
                )
            ).let { scheduleResponse ->
                if (scheduleResponse != null) {
                    Timber.d("manseeyy already scheduled")
                    scheduleId = scheduleResponse.planningHorizon.start
                    scheduleFhirId = scheduleResponse.scheduleId
                    scheduleRepository.updateSchedule(
                        scheduleResponse.copy(
                            bookedSlots = scheduleResponse.bookedSlots!! + 1
                        )
                    )
                } else {
                    val uuid = UUIDBuilder.generateUUID()
                    scheduleRepository.insertSchedule(
                        ScheduleResponse(
                            uuid = uuid,
                            scheduleId = null,
                            bookedSlots = 1,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            planningHorizon = Slot(
                                start = Date(
                                    selectedSlot.toCurrentTimeInMillis(
                                        Date()
                                    )
                                ),
                                end = Date(
                                    selectedSlot.to30MinutesAfter(
                                        Date()
                                    )
                                )
                            )
                        )
                    )
                    genericRepository.insertSchedule(
                        ScheduleResponse(
                            uuid = uuid,
                            scheduleId = null,
                            bookedSlots = null,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            planningHorizon = Slot(
                                start = Date(
                                    selectedSlot.toCurrentTimeInMillis(
                                        Date()
                                    )
                                ),
                                end = Date(
                                    selectedSlot.to30MinutesAfter(
                                        Date()
                                    )
                                )
                            )
                        )
                    )
                }
            }.also {
                val appointmentId = UUIDBuilder.generateUUID()
                val createdOn = Date()
                val slot = Slot(
                    start = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                    end = Date(
                        Date().toAppointmentTime().to5MinutesAfter(
                            Date()
                        )
                    )
                )
                addedToQueue(
                    appointmentRepository.addAppointment(
                        AppointmentResponseLocal(
                            appointmentId = null,
                            uuid = appointmentId,
                            patientId = patient.id,
                            scheduleId = scheduleId,
                            createdOn = createdOn,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            slot = slot,
                            status = AppointmentStatusEnum.WALK_IN.value
                        )
                    ).also {
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                appointmentId = null,
                                uuid = appointmentId,
                                patientFhirId = patient.fhirId ?: patient.id,
                                scheduleId = scheduleFhirId
                                    ?: scheduleRepository.getScheduleByStartTime(scheduleId.time)?.uuid!!,
                                createdOn = createdOn,
                                orgId = preferenceRepository.getOrganizationFhirId(),
                                slot = slot,
                                status = AppointmentStatusEnum.WALK_IN.value
                            )
                        )
                        updatePatientLastUpdated(patient.id)
                    }
                )
            }
        }
    }

    internal fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updated(
                appointmentRepository.updateAppointment(
                    appointment.copy(
                        status = AppointmentStatusEnum.ARRIVED.value
                    )
                ).also {
                    if (appointment.appointmentId.isNullOrBlank()) {
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                appointmentId = null,
                                createdOn = appointment.createdOn,
                                uuid = appointment.uuid,
                                patientFhirId = patient.fhirId ?: patient.id,
                                orgId = preferenceRepository.getOrganizationFhirId(),
                                scheduleId = scheduleRepository.getScheduleByStartTime(appointment.scheduleId.time)?.scheduleId
                                    ?: scheduleRepository.getScheduleByStartTime(appointment.scheduleId.time)?.uuid!!,
                                slot = appointment.slot,
                                status = AppointmentStatusEnum.ARRIVED.value
                            )
                        )
                    } else {
                        genericRepository.insertOrUpdateAppointmentPatch(
                            appointmentFhirId = appointment.appointmentId,
                            map = mapOf(
                                Pair(
                                    "status",
                                    ChangeRequest(
                                        operation = ChangeTypeEnum.REPLACE.value,
                                        value = AppointmentStatusEnum.ARRIVED.value
                                    )
                                )
                            )
                        )
                        updatePatientLastUpdated(patient.id)
                    }
                }
            )
        }
    }

    private suspend fun updatePatientLastUpdated(patientId: String) {
        val patientLastUpdatedResponse = PatientLastUpdatedResponse(
            uuid = patientId,
            timestamp = Date()
        )
        patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
        genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
    }
}