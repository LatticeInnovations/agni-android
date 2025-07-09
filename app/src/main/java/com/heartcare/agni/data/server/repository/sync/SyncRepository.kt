package com.heartcare.agni.data.server.repository.sync

import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.dispense.response.DispenseData
import com.heartcare.agni.data.server.model.dispense.response.MedicineDispenseResponse
import com.heartcare.agni.data.server.model.labormed.labtest.LabTestResponse
import com.heartcare.agni.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicineTimeResponse
import com.heartcare.agni.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.relatedperson.RelatedPersonResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationResponse
import com.heartcare.agni.data.server.model.vaccination.ManufacturerResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

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
    suspend fun getAndInsertLevelsData(): ResponseMapper<List<LevelResponse>>

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