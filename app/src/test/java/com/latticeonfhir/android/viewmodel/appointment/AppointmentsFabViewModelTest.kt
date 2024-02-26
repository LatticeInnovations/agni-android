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
import com.latticeonfhir.android.ui.common.appointmentsfab.AppointmentsFabViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
class AppointmentsFabViewModelTest : BaseClass() {
    @Mock
    lateinit var appointmentRepository: AppointmentRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Mock
    lateinit var scheduleRepository: ScheduleRepository

    @Mock
    lateinit var preferenceRepository: PreferenceRepository
    lateinit var viewModel: AppointmentsFabViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = AppointmentsFabViewModel(
            appointmentRepository,
            scheduleRepository,
            genericRepository,
            preferenceRepository
        )
    }

    @Test
    fun initialize_test() = runTest {
        `when`(appointmentRepository.getAppointmentsOfPatientByDate(
            id,
            Date().toTodayStartDate(),
            Date().toEndOfDay()
        )).thenReturn(null)
        `when`(appointmentRepository.getAppointmentListByDate(
            Date().toTodayStartDate(),
            Date().toEndOfDay()
        )).thenReturn(
            listOf(appointmentResponseLocal)
        )
        `when`(appointmentRepository.getAppointmentsOfPatientByStatus(
            id,
            AppointmentStatusEnum.SCHEDULED.value
        )).thenReturn(listOf(appointmentResponseLocal))
        viewModel.initialize(id)
        delay(5000)
        assertEquals(false, viewModel.ifAlreadyWaiting)
        assertEquals(false, viewModel.ifAllSlotsBooked)
        assertEquals(appointmentResponseLocal, viewModel.appointment)
    }


    @Test
    fun updateStatusToArrivedTest_appointment_id_is_not_null() = runTest {
        viewModel.appointment = appointmentResponseLocal
        `when`(
            appointmentRepository.updateAppointment(
                appointmentResponseLocal.copy(
                    status = AppointmentStatusEnum.ARRIVED.value
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
                            value = AppointmentStatusEnum.ARRIVED.value
                        )
                    )
                )
            )
        ).thenReturn(-1L)
        viewModel.updateStatusToArrived(patientResponse, appointmentResponseLocal) {
            assertEquals(1, it)
        }
    }

    @Test
    fun updateStatusToArrivedTest_appointment_id_is_null() = runTest {
        viewModel.appointment = appointmentResponseLocalNullFhirId
        `when`(
            appointmentRepository.updateAppointment(
                appointmentResponseLocalNullFhirId.copy(
                    status = AppointmentStatusEnum.ARRIVED.value
                )
            )
        ).thenReturn(1)
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocalNullFhirId.scheduleId.time))
            .thenReturn(
                scheduleResponse
            )
        `when`(scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time))
            .thenReturn(scheduleResponse)
        `when`(
            genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = null,
                    createdOn = appointmentResponseLocalNullFhirId.createdOn,
                    orgId = appointmentResponseLocalNullFhirId.orgId,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = scheduleResponse.uuid,
                    slot = appointmentResponseLocalNullFhirId.slot,
                    status = AppointmentStatusEnum.ARRIVED.value,
                    uuid = appointmentResponseLocalNullFhirId.uuid
                )
            )
        ).thenReturn(-1L)
        viewModel.updateStatusToArrived(patientResponse, appointmentResponseLocal) {
            assertEquals(1, it)
        }
    }

    @Test
    fun addPatientToQueueTest_existing_schedule() = runTest {
        `when`(
            scheduleRepository.getScheduleByStartTime(
                date.toSlotStartTime().toCurrentTimeInMillis(
                    date
                )
            )
        ).thenReturn(scheduleResponse)
        `when`(
            scheduleRepository.updateSchedule(
                scheduleResponse.copy(
                    bookedSlots = scheduleResponse.bookedSlots!! + 1
                )
            )
        ).thenReturn(1)
        `when`(preferenceRepository.getOrganizationFhirId())
            .thenReturn(appointmentResponseLocal.orgId)
        val slot = Slot(
            start = Date(date.toAppointmentTime().toCurrentTimeInMillis(date)),
            end = Date(
                date.toAppointmentTime().to5MinutesAfter(
                    date
                )
            )
        )
        `when`(
            appointmentRepository.addAppointment(
                AppointmentResponseLocal(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientId = id,
                    scheduleId = scheduleResponse.planningHorizon.start,
                    createdOn = appointmentResponseLocal.createdOn,
                    orgId = appointmentResponseLocal.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.WALK_IN.value
                )
            )
        ).thenReturn(listOf(-1L))
        `when`(
            genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = scheduleResponse.scheduleId!!,
                    createdOn = appointmentResponseLocal.createdOn,
                    orgId = appointmentResponseLocal.orgId,
                    slot = slot,
                    status = AppointmentStatusEnum.WALK_IN.value
                )
            )
        ).thenReturn(-1L)
        viewModel.addPatientToQueue(patientResponse) {
            assertEquals(listOf(-1L), it)
        }
    }

    @Test
    fun addPatientToQueueTest_new_schedule() = runTest {
        `when`(preferenceRepository.getOrganizationFhirId())
            .thenReturn(appointmentResponseLocal.orgId)
        `when`(
            scheduleRepository.getScheduleByStartTime(
                date.toSlotStartTime().toCurrentTimeInMillis(
                    date
                )
            )
        ).thenReturn(null)
        `when`(
            scheduleRepository.insertSchedule(
                ScheduleResponse(
                    uuid = scheduleResponse.uuid,
                    scheduleId = null,
                    bookedSlots = 1,
                    orgId = scheduleResponse.orgId,
                    planningHorizon = Slot(
                        start = Date(
                            date.toSlotStartTime().toCurrentTimeInMillis(
                                date
                            )
                        ),
                        end = Date(
                            date.toSlotStartTime().to30MinutesAfter(
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
                    uuid = scheduleResponse.uuid,
                    scheduleId = null,
                    bookedSlots = null,
                    orgId = preferenceRepository.getOrganizationFhirId(),
                    planningHorizon = Slot(
                        start = Date(
                            date.toSlotStartTime().toCurrentTimeInMillis(
                                date
                            )
                        ),
                        end = Date(
                            date.toSlotStartTime().to30MinutesAfter(
                                date
                            )
                        )
                    )
                )
            )
        ).thenReturn(-1L)
        `when`(
            appointmentRepository.addAppointment(
                AppointmentResponseLocal(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientId = id,
                    scheduleId = scheduleResponse.planningHorizon.start,
                    createdOn = appointmentResponseLocal.createdOn,
                    orgId = appointmentResponseLocal.orgId,
                    slot = appointmentResponseLocal.slot,
                    status = AppointmentStatusEnum.WALK_IN.value
                )
            )
        ).thenReturn(listOf(-1L))
        `when`(
            genericRepository.insertAppointment(
                AppointmentResponse(
                    appointmentId = null,
                    uuid = appointmentResponseLocal.uuid,
                    patientFhirId = patientResponse.fhirId,
                    scheduleId = scheduleResponse.scheduleId!!,
                    createdOn = appointmentResponseLocal.createdOn,
                    orgId = appointmentResponseLocal.orgId,
                    slot = appointmentResponseLocal.slot,
                    status = AppointmentStatusEnum.WALK_IN.value
                )
            )
        ).thenReturn(-1L)
        viewModel.addPatientToQueue(patientResponse) {
            assertEquals(listOf(-1L), it)
        }
    }
}