package com.latticeonfhir.core.data.repository.server.sync

import com.latticeonfhir.core.model.server.create.CreateResponse
import com.latticeonfhir.core.model.server.cvd.CVDResponse
import com.latticeonfhir.core.model.server.dispense.response.DispenseData
import com.latticeonfhir.core.model.server.dispense.response.MedicineDispenseResponse
import com.latticeonfhir.core.model.server.labormed.labtest.LabTestResponse
import com.latticeonfhir.core.model.server.labormed.medicalrecord.MedicalRecordResponse
import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.model.server.prescription.medication.MedicationResponse
import com.latticeonfhir.core.model.server.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.core.model.server.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.core.model.server.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.core.model.server.relatedperson.RelatedPersonResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.model.server.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.latticeonfhir.core.model.server.vaccination.ImmunizationResponse
import com.latticeonfhir.core.model.server.vaccination.ManufacturerResponse
import com.latticeonfhir.core.model.server.vitals.VitalResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper

interface SyncRepository {

    suspend fun getAndInsertListPatientData(offset: Int): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertRelation(): ResponseMapper<List<RelatedPersonResponse>>
    suspend fun getAndInsertPhotoPrescription(patientId: String?): ResponseMapper<List<PrescriptionPhotoResponse>>
    suspend fun getAndInsertFormPrescription(patientId: String?): ResponseMapper<List<PrescriptionResponse>>
    suspend fun getAndInsertMedication(offset: Int): ResponseMapper<List<MedicationResponse>>
    suspend fun getMedicineTime(): ResponseMapper<List<MedicineTimeResponse>>
    suspend fun getAndInsertSchedule(offset: Int): ResponseMapper<List<ScheduleResponse>>
    suspend fun getAndInsertAppointment(offset: Int): ResponseMapper<List<AppointmentResponse>>
    suspend fun getAndInsertPatientLastUpdatedData(): ResponseMapper<List<PatientLastUpdatedResponse>>
    suspend fun getAndInsertCVD(offset: Int): ResponseMapper<List<CVDResponse>>
    suspend fun getAndInsertListVitalData(offset: Int): ResponseMapper<List<VitalResponse>>
    suspend fun getAndInsertListSymptomsAndDiagnosisData(offset: Int): ResponseMapper<List<SymptomsAndDiagnosisResponse>>
    suspend fun getAndInsertListLabTestData(offset: Int): ResponseMapper<List<LabTestResponse>>
    suspend fun getAndInsertListMedicalRecordData(offset: Int): ResponseMapper<List<MedicalRecordResponse>>
    suspend fun getAndInsertDispense(patientId: String?): ResponseMapper<List<MedicineDispenseResponse>>
    suspend fun getAndInsertOTC(patientId: String?): ResponseMapper<List<DispenseData>>
    suspend fun getAndInsertImmunization(patientId: String?): ResponseMapper<List<ImmunizationResponse>>
    suspend fun getAndInsertManufacturer(): ResponseMapper<List<ManufacturerResponse>>

    //POST
    suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRelatedPersonPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendFormPrescriptionPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPhotoPrescriptionPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendSchedulePostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAppointmentPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPatientLastUpdatePostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendCVDPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendVitalPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendSymptomsAndDiagnosisPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendLabTestPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendMedRecordPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendDispensePostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendImmunizationPostData(): ResponseMapper<List<CreateResponse>>

    //PATCH
    suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAppointmentPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPrescriptionPhotoPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendCVDPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendVitalPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendSymptomsAndDiagnosisPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendLabTestPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendMedRecordPatchData(): ResponseMapper<List<CreateResponse>>

    //DELETE
    suspend fun deletePrescriptionPhoto(): ResponseMapper<List<CreateResponse>>
    suspend fun deleteLabTestPhoto(): ResponseMapper<List<CreateResponse>>
    suspend fun deleteMedTestPhoto(): ResponseMapper<List<CreateResponse>>
}