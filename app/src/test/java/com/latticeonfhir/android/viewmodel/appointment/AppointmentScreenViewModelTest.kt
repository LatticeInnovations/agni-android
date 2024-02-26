package com.latticeonfhir.android.viewmodel.appointment

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.ui.appointments.AppointmentsScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.test.*
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AppointmentScreenViewModelTest : BaseClass() {
    @Mock
    lateinit var appointmentRepository: AppointmentRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Mock
    lateinit var scheduleRepository: ScheduleRepository

    lateinit var viewModel: AppointmentsScreenViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = AppointmentsScreenViewModel(
            appointmentRepository,
            genericRepository,
            scheduleRepository
        )
    }

    @Test
    fun getAppointmentsListTest() = runBlocking {
        `when`(
            appointmentRepository.getAppointmentsOfPatientByStatus(
                id,
                AppointmentStatusEnum.SCHEDULED.value
            )
        ).thenReturn(
            listOf(appointmentResponseLocal)
        )
        `when`(
            appointmentRepository.getAppointmentsOfPatientByStatus(
                id,
                AppointmentStatusEnum.SCHEDULED.value
            )
        ).thenReturn(
            listOf(appointmentResponseLocal)
        )
        `when`(
            appointmentRepository.getAppointmentsOfPatientByStatus(
                id,
                AppointmentStatusEnum.COMPLETED.value
            )
        ).thenReturn(listOf(completedAppointmentResponseLocal))

        viewModel.getAppointmentsList(id)
        delay(5000)
        assertEquals(listOf(appointmentResponseLocal), viewModel.upcomingAppointmentsList)
        assertEquals(listOf(completedAppointmentResponseLocal), viewModel.completedAppointmentsList)
    }

    @Test
    fun cancelAppointmentTest_when_appointment_fhir_id_is_not_null() = runTest {
        viewModel.selectedAppointment = appointmentResponseLocal
        viewModel.patient = patientResponse
        `when`(
            appointmentRepository.updateAppointment(
                appointmentResponseLocal.copy(
                    status = AppointmentStatusEnum.CANCELLED.value
                )
            )
        ).thenReturn(1)
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

        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)).thenReturn(
            scheduleResponse
        )
        `when`(
            genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = appointmentResponseLocal.uuid,
                    uuid = appointmentResponseLocal.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = scheduleResponse.scheduleId!!,
                    slot = appointmentResponseLocal.slot,
                    orgId = appointmentResponseLocal.orgId,
                    createdOn = appointmentResponseLocal.createdOn,
                    status = AppointmentStatusEnum.CANCELLED.value
                )
            )
        ).thenReturn(-1L)
        `when`(
            genericRepository.insertOrUpdateAppointmentPatch(
                appointmentFhirId = appointmentResponseLocal.appointmentId!!,
                map = mapOf(
                    Pair(
                        "status",
                        ChangeRequest(
                            value = AppointmentStatusEnum.CANCELLED.value,
                            operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                )
            )
        ).thenReturn(-1L)
        viewModel.cancelAppointment {
            assertEquals(1, it)
        }
    }

    @Test
    fun cancelAppointmentTest_when_appointment_fhir_id_is_null() = runTest {
        viewModel.selectedAppointment = appointmentResponseLocalNullFhirId
        viewModel.patient = patientResponse
        `when`(
            appointmentRepository.updateAppointment(
                appointmentResponseLocalNullFhirId.copy(
                    status = AppointmentStatusEnum.CANCELLED.value
                )
            )
        ).thenReturn(1)
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocalNullFhirId.scheduleId.time)).thenReturn(
            scheduleResponse
        )
        `when`(
            scheduleRepository.updateSchedule(
                scheduleResponse.copy(
                    bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                )
            )
        ).thenReturn(1)

        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocalNullFhirId.scheduleId.time)).thenReturn(
            scheduleResponse
        )
        `when`(
            genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = appointmentResponseLocalNullFhirId.uuid,
                    uuid = appointmentResponseLocalNullFhirId.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = scheduleResponse.scheduleId!!,
                    slot = appointmentResponseLocalNullFhirId.slot,
                    orgId = appointmentResponseLocalNullFhirId.orgId,
                    createdOn = appointmentResponseLocalNullFhirId.createdOn,
                    status = AppointmentStatusEnum.CANCELLED.value
                )
            )
        ).thenReturn(-1L)
        viewModel.cancelAppointment {
            assertEquals(1, it)
        }
    }
}