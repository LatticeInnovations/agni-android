package com.latticeonfhir.android.viewmodel.appointment

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.ui.appointments.schedule.ScheduleAppointmentViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ScheduleAppointmentViewModelTest : BaseClass() {
    @Mock
    lateinit var scheduleRepository: ScheduleRepository

    @Mock
    lateinit var appointmentRepository: AppointmentRepository

    @Mock
    lateinit var preferenceRepository: PreferenceRepository

    @Mock
    lateinit var genericRepository: GenericRepository
    lateinit var viewModel: ScheduleAppointmentViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ScheduleAppointmentViewModel(
            scheduleRepository,
            appointmentRepository,
            preferenceRepository,
            genericRepository
        )
    }

    @Test
    fun getBookedSlotsCountTest() = runTest {
        `when`(scheduleRepository.getBookedSlotsCount(date.time)).thenReturn(1)
        viewModel.getBookedSlotsCount(date.time) {
            assertEquals(1, it)
        }
    }

    @Test
    fun insertScheduleAndAppointmentTest_existing_schedule_appointment_fhir_id_not_null_same_day_appointment() =
        runTest {
            viewModel.patient = patientResponse
            viewModel.selectedDate = date
            viewModel.selectedSlot = date.toAppointmentTime()
            `when`(
                appointmentRepository.getAppointmentsOfPatientByDate(
                    id,
                    date.toTodayStartDate(),
                    date.toEndOfDay()
                )
            ).thenReturn(appointmentResponseLocal)
            `when`(
                scheduleRepository.getScheduleByStartTime(
                    viewModel.selectedSlot.toCurrentTimeInMillis(
                        viewModel.selectedDate
                    )
                )
            ).thenReturn(scheduleResponse)
            `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)).thenReturn(
                scheduleResponse
            )
            `when`(
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(
                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                    )
                )
            ).thenReturn(1)
            `when`(
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(
                        bookedSlots = scheduleResponse.bookedSlots!! + 1
                    )
                )
            ).thenReturn(1)
            val slot = Slot(
                start = Date(
                    viewModel.selectedSlot.toCurrentTimeInMillis(
                        date
                    )
                ),
                end = Date(
                    viewModel.selectedSlot.to5MinutesAfter(
                        date
                    )
                )
            )
            `when`(
                appointmentRepository.updateAppointment(
                    appointmentResponseLocal.copy(
                        scheduleId = date,
                        createdOn = date,
                        slot = slot
                    )
                )
            ).thenReturn(1)
            `when`(
                genericRepository.insertOrUpdateAppointmentPatch(
                    appointmentFhirId = appointmentResponseLocal.appointmentId!!,
                    map = mapOf(
                        Pair(
                            "status",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = AppointmentStatusEnum.SCHEDULED.value
                            )
                        ),
                        Pair(
                            "slot",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = slot
                            )
                        ),
                        Pair(
                            "scheduleId",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = id
                            )
                        ),
                        Pair(
                            "createdOn",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = date
                            )
                        )
                    )
                )
            ).thenReturn(-1L)
            viewModel.insertScheduleAndAppointment {
                assertEquals(1, it)
            }
        }

    @Test
    fun insertScheduleAndAppointmentTest_new_schedule_appointment_fhir_id_not_null_same_day_appointment() =
        runTest {
            viewModel.patient = patientResponse
            viewModel.selectedDate = date
            viewModel.selectedSlot = date.toAppointmentTime()
            `when`(
                appointmentRepository.getAppointmentsOfPatientByDate(
                    id,
                    date.toTodayStartDate(),
                    date.toEndOfDay()
                )
            ).thenReturn(appointmentResponseLocal)
            `when`(
                scheduleRepository.getScheduleByStartTime(
                    appointmentResponseLocal.scheduleId.time
                )
            ).thenReturn(scheduleResponse)
            `when`(
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(
                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                    )
                )
            ).thenReturn(1)
            `when`(preferenceRepository.getOrganizationFhirId()).thenReturn(scheduleResponse.orgId)
            `when`(
                scheduleRepository.insertSchedule(
                    ScheduleResponse(
                        uuid = id,
                        scheduleId = null,
                        bookedSlots = 1,
                        orgId = scheduleResponse.orgId,
                        planningHorizon = Slot(
                            start = Date(
                                viewModel.selectedSlot.toCurrentTimeInMillis(
                                    date
                                )
                            ),
                            end = Date(
                                viewModel.selectedSlot.to30MinutesAfter(
                                    date
                                )
                            )
                        )
                    )
                )
            ).thenReturn(listOf(-1L))
            `when`(
                genericRepository.insertSchedule(
                    ScheduleResponse(
                        uuid = id,
                        scheduleId = null,
                        bookedSlots = null,
                        orgId = scheduleResponse.orgId,
                        planningHorizon = Slot(
                            start = Date(
                                viewModel.selectedSlot.toCurrentTimeInMillis(
                                    date
                                )
                            ),
                            end = Date(
                                viewModel.selectedSlot.to30MinutesAfter(
                                    date
                                )
                            )
                        )
                    )
                )
            ).thenReturn(-1L)
            val slot = Slot(
                start = Date(
                    viewModel.selectedSlot.toCurrentTimeInMillis(
                        date
                    )
                ),
                end = Date(
                    viewModel.selectedSlot.to5MinutesAfter(
                        date
                    )
                )
            )
            `when`(
                appointmentRepository.updateAppointment(
                    appointmentResponseLocal.copy(
                        scheduleId = date,
                        createdOn = date,
                        slot = slot
                    )
                )
            ).thenReturn(1)
            `when`(
                genericRepository.insertOrUpdateAppointmentPatch(
                    appointmentFhirId = appointmentResponseLocal.appointmentId!!,
                    map = mapOf(
                        Pair(
                            "status",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = AppointmentStatusEnum.SCHEDULED.value
                            )
                        ),
                        Pair(
                            "slot",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = slot
                            )
                        ),
                        Pair(
                            "scheduleId",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = id
                            )
                        ),
                        Pair(
                            "createdOn",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = date
                            )
                        )
                    )
                )
            ).thenReturn(-1L)
            viewModel.insertScheduleAndAppointment {
                assertEquals(1, it)
            }
        }

    @Test
    fun insertScheduleAndAppointmentTest_existing_schedule_new_day_appointment() =
        runTest {
            viewModel.patient = patientResponse
            viewModel.selectedDate = date
            viewModel.selectedSlot = date.toAppointmentTime()
            `when`(preferenceRepository.getOrganizationFhirId()).thenReturn(scheduleResponse.orgId)
            `when`(
                appointmentRepository.getAppointmentsOfPatientByDate(
                    id,
                    date.toTodayStartDate(),
                    date.toEndOfDay()
                )
            ).thenReturn(null)
            `when`(scheduleRepository.getScheduleByStartTime(
                viewModel.selectedSlot.toCurrentTimeInMillis(
                    viewModel.selectedDate
                )
            )).thenReturn(scheduleResponse)
            `when`(scheduleRepository.updateSchedule(
                scheduleResponse.copy(
                    bookedSlots = scheduleResponse.bookedSlots!! + 1
                )
            )).thenReturn(1)
            val slot = Slot(
                start = Date(
                    viewModel.selectedSlot.toCurrentTimeInMillis(
                        date
                    )
                ),
                end = Date(
                    viewModel.selectedSlot.to5MinutesAfter(
                        date
                    )
                )
            )
            `when`(appointmentRepository.addAppointment(
                AppointmentResponseLocal(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientId = id,
                    scheduleId = date,
                    createdOn = date,
                    orgId = scheduleResponse.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.SCHEDULED.value
                )
            )).thenReturn(listOf(-1L))
            `when`(genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = id,
                    createdOn = date,
                    orgId = scheduleResponse.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.SCHEDULED.value
                )
            )).thenReturn(-1L)
            viewModel.insertScheduleAndAppointment {
                assertEquals(1, it)
            }
        }

    @Test
    fun insertScheduleAndAppointmentTest_new_schedule_new_day_appointment() =
        runTest {
            viewModel.patient = patientResponse
            viewModel.selectedDate = date
            viewModel.selectedSlot = date.toAppointmentTime()
            `when`(preferenceRepository.getOrganizationFhirId()).thenReturn(scheduleResponse.orgId)
            `when`(
                appointmentRepository.getAppointmentsOfPatientByDate(
                    id,
                    date.toTodayStartDate(),
                    date.toEndOfDay()
                )
            ).thenReturn(null)
            `when`(scheduleRepository.getScheduleByStartTime(
                viewModel.selectedSlot.toCurrentTimeInMillis(
                    viewModel.selectedDate
                )
            )).thenReturn(null)
            `when`(scheduleRepository.insertSchedule(
                ScheduleResponse(
                    uuid = id,
                    scheduleId = null,
                    bookedSlots = 1,
                    orgId = scheduleResponse.orgId,
                    planningHorizon = Slot(
                        start = Date(
                            viewModel.selectedSlot.toCurrentTimeInMillis(
                                date
                            )
                        ),
                        end = Date(
                            viewModel.selectedSlot.to30MinutesAfter(
                                date
                            )
                        )
                    )
                )
            )).thenReturn(listOf(-1L))
            `when`(genericRepository.insertSchedule(
                ScheduleResponse(
                    uuid = id,
                    scheduleId = null,
                    bookedSlots = null,
                    orgId = preferenceRepository.getOrganizationFhirId(),
                    planningHorizon = Slot(
                        start = Date(
                            viewModel.selectedSlot.toCurrentTimeInMillis(
                                date
                            )
                        ),
                        end = Date(
                            viewModel.selectedSlot.to30MinutesAfter(
                                date
                            )
                        )
                    )
                )
            )).thenReturn(-1L)
            val slot = Slot(
                start = Date(
                    viewModel.selectedSlot.toCurrentTimeInMillis(
                        date
                    )
                ),
                end = Date(
                    viewModel.selectedSlot.to5MinutesAfter(
                        date
                    )
                )
            )
            `when`(appointmentRepository.addAppointment(
                AppointmentResponseLocal(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientId = id,
                    scheduleId = date,
                    createdOn = date,
                    orgId = scheduleResponse.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.SCHEDULED.value
                )
            )).thenReturn(listOf(-1L))
            `when`(genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = id,
                    createdOn = date,
                    orgId = scheduleResponse.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.SCHEDULED.value
                )
            )).thenReturn(-1L)
            viewModel.insertScheduleAndAppointment {

            }
        }
}