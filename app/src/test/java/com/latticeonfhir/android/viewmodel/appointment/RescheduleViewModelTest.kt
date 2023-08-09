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
import com.latticeonfhir.android.ui.appointments.reschedule.RescheduleAppointmentViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.test.*
import org.mockito.Mockito.`when`
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class RescheduleViewModelTest : BaseClass() {
    @Mock
    lateinit var scheduleRepository: ScheduleRepository

    @Mock
    lateinit var appointmentRepository: AppointmentRepository

    @Mock
    lateinit var preferenceRepository: PreferenceRepository

    @Mock
    lateinit var genericRepository: GenericRepository
    lateinit var viewModel: RescheduleAppointmentViewModel


    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = RescheduleAppointmentViewModel(
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
    fun rescheduleAppointment_existing_schedule_appointment_id_not_null() = runTest {
        viewModel.appointment = appointmentResponseLocal
        viewModel.patient = patientResponse
        viewModel.selectedDate = date
        viewModel.selectedSlot = date.toAppointmentTime()
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
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)).thenReturn(scheduleResponse)
        `when`(scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
            )
        )).thenReturn(1)
        `when`(scheduleRepository.getScheduleByStartTime(
            viewModel.selectedSlot.toCurrentTimeInMillis(
                date
            )
        )).thenReturn(scheduleResponse)
        `when`(scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots!! + 1
            )
        )).thenReturn(1)
        `when`(appointmentRepository.updateAppointment(
            AppointmentResponseLocal(
                appointmentId = appointmentResponseLocal.appointmentId,
                uuid = appointmentResponseLocal.uuid,
                scheduleId = date,
                createdOn = date,
                slot = slot,
                orgId = appointmentResponseLocal.orgId,
                patientId = id,
                status = appointmentResponseLocal.status
            )
        )).thenReturn(1)
        `when`(genericRepository.insertOrUpdateAppointmentPatch(
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
        )).thenReturn(-1L)
        viewModel.rescheduleAppointment {
            assertEquals(1, it)
        }
    }

    @Test
    fun rescheduleAppointment_existing_schedule_appointment_id_null() = runTest {
        viewModel.appointment = appointmentResponseLocalNullFhirId
        viewModel.patient = patientResponse
        viewModel.selectedDate = date
        viewModel.selectedSlot = date.toAppointmentTime()
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
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocalNullFhirId.scheduleId.time)).thenReturn(scheduleResponse)
        `when`(scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
            )
        )).thenReturn(1)
        `when`(scheduleRepository.getScheduleByStartTime(
            viewModel.selectedSlot.toCurrentTimeInMillis(
                date
            )
        )).thenReturn(scheduleResponse)
        `when`(scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots!! + 1
            )
        )).thenReturn(1)
        `when`(appointmentRepository.updateAppointment(
            AppointmentResponseLocal(
                appointmentId = appointmentResponseLocalNullFhirId.appointmentId,
                uuid = appointmentResponseLocalNullFhirId.uuid,
                scheduleId = date,
                createdOn = date,
                slot = slot,
                orgId = appointmentResponseLocalNullFhirId.orgId,
                patientId = id,
                status = appointmentResponseLocalNullFhirId.status
            )
        )).thenReturn(1)
        `when`(genericRepository.insertAppointment(
            AppointmentResponse(
                scheduleId = id,
                createdOn = date,
                slot = slot,
                patientFhirId = patientResponse.fhirId,
                appointmentId = appointmentResponseLocalNullFhirId.appointmentId,
                orgId = appointmentResponseLocalNullFhirId.orgId,
                status = appointmentResponseLocalNullFhirId.status,
                uuid = appointmentResponseLocalNullFhirId.uuid
            )
        )).thenReturn(-1L)
        viewModel.rescheduleAppointment {
            assertEquals(1, it)
        }
    }

    @Test
    fun rescheduleAppointment_new_schedule_appointment_id_not_null() = runTest {
        viewModel.appointment = appointmentResponseLocal
        viewModel.patient = patientResponse
        viewModel.selectedDate = date
        viewModel.selectedSlot = date.toAppointmentTime()
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
        `when`(preferenceRepository.getOrganizationFhirId()).thenReturn(scheduleResponse.orgId)
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)).thenReturn(scheduleResponse)
        `when`(scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
            )
        )).thenReturn(1)
        `when`(scheduleRepository.getScheduleByStartTime(
            viewModel.selectedSlot.toCurrentTimeInMillis(
                date
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
        `when`(appointmentRepository.updateAppointment(
            AppointmentResponseLocal(
                appointmentId = appointmentResponseLocal.appointmentId,
                uuid = appointmentResponseLocal.uuid,
                scheduleId = date,
                createdOn = date,
                slot = slot,
                orgId = appointmentResponseLocal.orgId,
                patientId = id,
                status = appointmentResponseLocal.status
            )
        )).thenReturn(1)
        `when`(genericRepository.insertOrUpdateAppointmentPatch(
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
        )).thenReturn(-1L)
        viewModel.rescheduleAppointment {
            assertEquals(1, it)
        }
    }
}