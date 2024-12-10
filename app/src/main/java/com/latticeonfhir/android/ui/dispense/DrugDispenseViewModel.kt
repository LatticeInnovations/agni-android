package com.latticeonfhir.android.ui.dispense

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DrugDispenseViewModel @Inject constructor(
    private val dispenseRepository: DispenseRepository,
    private val medicationRepository: MedicationRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    val tabs = listOf("Prescription", "Dispense log")
    var prescriptionSelected by mutableStateOf<DispenseAndPrescriptionRelation?>(null)
    var prescriptionToDispense by mutableStateOf<DispenseAndPrescriptionRelation?>(null)
    var previousPrescriptionList by mutableStateOf(listOf<DispenseAndPrescriptionRelation>())
    var previousDispensed by mutableStateOf(listOf<DispensedPrescriptionInfo>())
    private var allMedications by mutableStateOf(listOf<MedicationStrengthRelation>())
    var isOTCDispensed by mutableStateOf(false)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddDispense by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var categoryClicked by mutableStateOf("")

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
                canAddDispense =
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

    internal fun getPrescriptionDispenseData(
        patientId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            previousPrescriptionList = dispenseRepository.getPrescriptionDispenseData(patientId)
            allMedications = medicationRepository.getAllMedication()
            previousDispensed = dispenseRepository.getDispensedPrescriptionInfoByPatientId(patientId)
        }
    }

    internal fun getMedNameFromMedFhirId(medFhirId: String): MedicationStrengthRelation {
        return allMedications.first {
            it.medicationEntity.medFhirId == medFhirId
        }
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