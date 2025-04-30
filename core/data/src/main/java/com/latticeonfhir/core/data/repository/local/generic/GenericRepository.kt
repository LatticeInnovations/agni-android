package com.latticeonfhir.core.data.repository.local.generic

import com.latticeonfhir.core.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.core.data.local.model.vital.VitalLocal
import com.latticeonfhir.core.model.enums.GenericTypeEnum
import com.latticeonfhir.core.model.enums.SyncType
import com.latticeonfhir.core.model.server.cvd.CVDResponse
import com.latticeonfhir.core.model.server.dispense.request.MedicineDispenseRequest
import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.model.server.prescription.photo.PrescriptionPhotoPatch
import com.latticeonfhir.core.model.server.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.core.model.server.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.core.model.server.relatedperson.RelatedPersonResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.model.server.vaccination.ImmunizationResponse
import com.latticeonfhir.core.utils.builders.UUIDBuilder

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
    suspend fun updateImmunizationFhirId()

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
        labTestId:String,
        typeEnum: GenericTypeEnum
    ): Long

    suspend fun insertOrUpdatePhotoLabTestAndMedPatch(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID(),
        typeEnum: GenericTypeEnum
    ): Long

    suspend fun insertImmunization(
        immunizationResponse: ImmunizationResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long
}