package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.dispense.request.MedicineDispenseRequest
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoPatch
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
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

    suspend fun insertPatient(
        patientResponse: PatientResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertRelation(
        patientId: String,
        relatedPersonResponse: RelatedPersonResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun updateRelationFhirId()

    suspend fun insertPrescription(
        prescriptionResponse: PrescriptionResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertPhotoPrescription(
        prescriptionPhotoResponse: PrescriptionPhotoResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun updatePrescriptionFhirId()

    suspend fun insertSchedule(
        scheduleResponse: ScheduleResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertAppointment(
        appointmentResponse: AppointmentResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertCVDRecord(
        cvdResponse: CVDResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertSymDiag(
        local: SymptomsAndDiagnosisData, uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertDispense(
        medicineDispenseRequest: MedicineDispenseRequest,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun updateAppointmentFhirIds()
    suspend fun updateAppointmentFhirIdInPatch()

    suspend fun updateCVDFhirIds()
    suspend fun updateVitalFhirId()
    suspend fun updateSymDiagFhirId()
    suspend fun updateLabTestFhirId()
    suspend fun updateMedRecordFhirId()
    suspend fun updateDispenseFhirId()

    suspend fun insertOrUpdatePatientPatchEntity(
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateAppointmentPatch(
        appointmentFhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdatePhotoPrescriptionPatch(
        prescriptionFhirId: String,
        prescriptionPhotoPatch: PrescriptionPhotoPatch,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateCVDPatch(
        cvdFhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateSymDiagPatchEntity(
        fhirId: String, map: Map<String, Any>, uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertVital(
        vitalLocal: VitalLocal, uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateVitalPatchEntity(
        vitalFhirId: String, map: Map<String, Any>, uuid: String = UUIDBuilder.generateUUID()
    ): Long
    suspend fun insertPatientLastUpdated(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun removeGenericRecord(id: String): Int
    suspend fun insertDeleteRequest(fhirId: String, typeEnum: GenericTypeEnum, syncType: SyncType): Long

    suspend fun insertPhotoLabTestAndMedRecord(
        map: Map<String, Any>,
        patientId: String,
        uuid: String = UUIDBuilder.generateUUID(),
        typeEnum: GenericTypeEnum
    ): Long

    suspend fun insertOrUpdatePhotoLabTestAndMedPatch(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID(),
        typeEnum: GenericTypeEnum
    ): Long
}