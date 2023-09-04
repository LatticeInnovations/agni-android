package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder

/**
 *
 * Here we are passing UUID in Parameters due to Unit Testing Scenario.
 * if we generate UUID in repo Unit tests were failing.
 * Do not pass uuid from anywhere else it will automatically generate here.
 *
 */
interface GenericRepository {

    suspend fun insertPatient(patientResponse: PatientResponse, uuid: String = UUIDBuilder.generateUUID()): Long

    suspend fun insertRelation(patientId: String, relatedPersonResponse: RelatedPersonResponse, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun updateRelationFhirId()

    suspend fun insertPrescription(prescriptionResponse: PrescriptionResponse, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun updatePrescriptionFhirId()

    suspend fun insertSchedule(scheduleResponse: ScheduleResponse, uuid: String = UUIDBuilder.generateUUID()): Long

    suspend fun insertAppointment(appointmentResponse: AppointmentResponse, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun updateAppointmentFhirIds()
    suspend fun updateAppointmentFhirIdInPatch()

    suspend fun insertOrUpdatePatchEntity(patientFhirId: String, map: Map<String, Any>, typeEnum: GenericTypeEnum, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun insertOrUpdatePatientPatch(patientFhirId: String, map: Map<String, Any>, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun insertOrUpdateRelationPatch(patientFhirId: String, map: Map<String, Any>, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun insertOrUpdateAppointmentPatch(appointmentFhirId: String, map: Map<String, Any>, uuid: String = UUIDBuilder.generateUUID()): Long
}