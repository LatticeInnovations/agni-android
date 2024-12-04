package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.model.vitals.VitalResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

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


    //PATCH
    suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAppointmentPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPrescriptionPhotoPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendCVDPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendVitalPatchData(): ResponseMapper<List<CreateResponse>>

}