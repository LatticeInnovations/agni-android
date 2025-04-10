package com.latticeonfhir.core.utils.common

import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.core.data.local.enums.AppointmentTypeEnum
import com.latticeonfhir.core.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.core.data.local.enums.LastVisit
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.core.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.core.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientAndIdentifierAndAppointmentEntity
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.core.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.core.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.core.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.lastMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.lastThreeMonth
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.lastWeek
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.lastYear
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import timber.log.Timber
import java.util.Date

object Queries {
    internal suspend fun addPatientToQueue(
        patient: PatientResponse,
        scheduleRepository: ScheduleRepository,
        genericRepository: GenericRepository,
        preferenceRepository: PreferenceRepository,
        appointmentRepository: AppointmentRepository,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        addedToQueue: (List<Long>) -> Unit
    ) {
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
                        status = AppointmentStatusEnum.WALK_IN.value,
                        appointmentType = AppointmentTypeEnum.WALK_IN.code,
                        inProgressTime = null
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
                            status = AppointmentStatusEnum.WALK_IN.value,
                            appointmentType = AppointmentTypeEnum.WALK_IN.code,
                            inProgressTime = null
                        )
                    )
                    updatePatientLastUpdated(
                        patient.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            )
        }
    }

    internal suspend fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        appointmentRepository: AppointmentRepository,
        genericRepository: GenericRepository,
        preferenceRepository: PreferenceRepository,
        scheduleRepository: ScheduleRepository,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        updated: (Int) -> Unit
    ) {
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
                            status = AppointmentStatusEnum.ARRIVED.value,
                            appointmentType = appointment.appointmentType,
                            inProgressTime = appointment.inProgressTime
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
                    updatePatientLastUpdated(
                        patient.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            }
        )
    }

     suspend fun updatePatientLastUpdated(
        patientId: String,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        genericRepository: GenericRepository
    ) {
        val patientLastUpdatedResponse = PatientLastUpdatedResponse(
            uuid = patientId,
            timestamp = Date()
        )
        patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
        genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
    }

    internal suspend fun getSearchListWithLastVisited(
        lastVisited: String,
        searchList: List<PatientAndIdentifierEntity>,
        appointmentRepository: AppointmentRepository
    ): List<PatientAndIdentifierEntity> {
        val listWithCompletedAppointment = mutableListOf<PatientAndIdentifierAndAppointmentEntity>()
        val fromTime = when (lastVisited) {
            LastVisit.LAST_WEEK.label -> lastWeek()
            LastVisit.LAST_MONTH.label -> lastMonth()
            LastVisit.LAST_THREE_MONTHS.label -> lastThreeMonth()
            LastVisit.LAST_YEAR.label -> lastYear()
            else -> Date(0L)
        }
        searchList.forEach { patientAndIdentifierEntity ->
            val lastCompletedAppointment =
                appointmentRepository.getLastCompletedAppointment(patientAndIdentifierEntity.patientEntity.id)
            if (lastCompletedAppointment != null && lastCompletedAppointment.startTime > fromTime) {
                listWithCompletedAppointment.add(
                    PatientAndIdentifierAndAppointmentEntity(
                        patientAndIdentifierEntity = patientAndIdentifierEntity,
                        appointmentEntity = lastCompletedAppointment
                    )
                )
            }
        }
        return listWithCompletedAppointment.sortedByDescending {
            it.appointmentEntity.startTime
        }.map {
            it.patientAndIdentifierEntity
        }
    }

    internal suspend fun checkAndUpdateAppointmentStatusToInProgress(
        inProgressTime: Date,
        patient: PatientResponse,
        appointmentResponseLocal: AppointmentResponseLocal,
        appointmentRepository: AppointmentRepository,
        genericRepository: GenericRepository,
        scheduleRepository: ScheduleRepository
    ) {
        if (appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value
            || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value) {
            appointmentRepository.updateAppointment(
                appointmentResponseLocal.copy(
                    status = AppointmentStatusEnum.IN_PROGRESS.value,
                    inProgressTime = inProgressTime
                )
            )
            if (appointmentResponseLocal.appointmentId.isNullOrBlank()) {
                genericRepository.insertAppointment(
                    AppointmentResponse(
                        appointmentId = null,
                        createdOn = appointmentResponseLocal.createdOn,
                        uuid = appointmentResponseLocal.uuid,
                        patientFhirId = patient.fhirId ?: patient.id,
                        orgId = appointmentResponseLocal.orgId,
                        scheduleId = scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)?.scheduleId
                            ?: scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)?.uuid!!,
                        slot = appointmentResponseLocal.slot,
                        status = AppointmentStatusEnum.IN_PROGRESS.value,
                        appointmentType = appointmentResponseLocal.appointmentType,
                        inProgressTime = inProgressTime
                    )
                )
            } else {
                genericRepository.insertOrUpdateAppointmentPatch(
                    appointmentFhirId = appointmentResponseLocal.appointmentId,
                    map = mapOf(
                        Pair(
                            "generatedOn",
                            inProgressTime
                        ),
                        Pair(
                            "status",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = AppointmentStatusEnum.IN_PROGRESS.value
                            )
                        )
                    )
                )
            }
        }
    }
}