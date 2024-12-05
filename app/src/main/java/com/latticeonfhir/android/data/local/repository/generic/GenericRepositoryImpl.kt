package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoPatch
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.constants.LabTestAndMedConstants
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

/**
 *
 * Here we are passing UUID in Parameters due to Unit Testing Scenario.
 * if we generate UUID in repo Unit tests were failing.
 * Do not pass uuid from anywhere else it will automatically generate here.
 *
 */
class GenericRepositoryImpl @Inject constructor(
    private val genericDao: GenericDao,
    patientDao: PatientDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao
) : GenericRepository,
    GenericRepositoryDatabaseTransactions(genericDao, patientDao, scheduleDao, appointmentDao) {

    override suspend fun insertPatient(patientResponse: PatientResponse, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = patientResponse.id,
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.POST
        ).let { patientGenericEntity ->
            insertPatientGenericEntity(patientResponse, patientGenericEntity, uuid)
        }
    }

    override suspend fun insertRelation(
        patientId: String,
        relatedPersonResponse: RelatedPersonResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientId,
            genericTypeEnum = GenericTypeEnum.RELATION,
            syncType = SyncType.POST
        ).let { relationGenericEntity ->
            insertRelationGenericEntity(
                relationGenericEntity,
                relatedPersonResponse,
                uuid,
                patientId
            )
        }
    }

    override suspend fun updateRelationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.RELATION).forEach { relationGenericEntity ->
            updateRelationFhirIdInGenericEntity(relationGenericEntity)
        }
    }

    override suspend fun insertPrescription(
        prescriptionResponse: PrescriptionResponse,
        uuid: String
    ): Long {
        return genericDao.insertGenericEntity(
            GenericEntity(
                id = uuid,
                patientId = prescriptionResponse.prescriptionId,
                payload = prescriptionResponse.toJson(),
                type = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            )
        )[0]
    }

    override suspend fun insertPhotoPrescription(
        prescriptionPhotoResponse: PrescriptionPhotoResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = prescriptionPhotoResponse.prescriptionId,
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
            syncType = SyncType.POST
        ).let { prescriptionGenericEntity ->
            insertPrescriptionPhotoGenericEntity(
                prescriptionPhotoResponse,
                prescriptionGenericEntity,
                uuid
            )
        }
    }

    override suspend fun updatePrescriptionFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION)
            .forEach { prescriptionGenericEntity ->
                updateFormPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
            }
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE)
            .forEach { prescriptionGenericEntity ->
                updatePhotoPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
            }
    }

    override suspend fun insertSchedule(scheduleResponse: ScheduleResponse, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = scheduleResponse.uuid,
            genericTypeEnum = GenericTypeEnum.SCHEDULE,
            syncType = SyncType.POST
        ).let { scheduleGenericEntity ->
            insertScheduleGenericEntity(scheduleGenericEntity, scheduleResponse, uuid)
        }
    }

    override suspend fun updateAppointmentFhirIds() {
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT)
            .forEach { appointmentGenericEntity ->
                updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity)
            }
    }

    override suspend fun updateAppointmentFhirIdInPatch() {
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT, SyncType.PATCH)
            .forEach { appointmentGenericEntity ->
                updateAppointmentFhirIdInGenericEntityPatch(appointmentGenericEntity)
            }
    }

    override suspend fun updateCVDFhirIds() {
        genericDao.getNotSyncedData(GenericTypeEnum.CVD)
            .forEach { cvdGenericEntity ->
                updateCVDFhirIdInGenericEntity(cvdGenericEntity)
            }
    }
    override suspend fun updateVitalFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.VITAL)
            .forEach { vitalGenericEntity ->
                updateVitalFhirIdInGenericEntity(vitalGenericEntity)
            }
    }
    override suspend fun updateSymDiagFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.SYMPTOMS_DIAGNOSIS)
            .forEach { symDiagGenericEntity ->
                updateSymDiagFhirIdInGenericEntity(symDiagGenericEntity)
            }
    }
    override suspend fun updateLabTestFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.LAB_TEST)
            .forEach { genericEntity ->
                updateLabTestFhirIdInGenericEntity(genericEntity)
            }
    }

    override suspend fun updateMedRecordFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.MEDICAL_RECORD)
            .forEach { genericEntity ->
                updateMedicalRecordFhirIdInGenericEntity(genericEntity)
            }
    }

    override suspend fun insertAppointment(
        appointmentResponse: AppointmentResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = appointmentResponse.uuid,
            genericTypeEnum = GenericTypeEnum.APPOINTMENT,
            syncType = SyncType.POST
        ).let { appointmentGenericEntity ->
            insertAppointmentGenericEntity(appointmentGenericEntity, appointmentResponse, uuid)
        }
    }

    override suspend fun insertCVDRecord(
        cvdResponse: CVDResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = cvdResponse.cvdUuid,
            genericTypeEnum = GenericTypeEnum.CVD,
            syncType = SyncType.POST
        ).let { cvdGenericEntity ->
            insertCVDGenericEntity(cvdGenericEntity, cvdResponse, uuid)
        }
    }
    override suspend fun insertSymDiag(local: SymptomsAndDiagnosisData, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = local.symDiagUuid,
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS,
            syncType = SyncType.POST
        ).let {
            insertSymDiagGenericEntity(local, it, uuid)
        }
    }

    override suspend fun insertVital(vitalLocal: VitalLocal, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = vitalLocal.vitalUuid,
            genericTypeEnum = GenericTypeEnum.VITAL,
            syncType = SyncType.POST
        ).let {
            insertVitalGenericEntity(vitalLocal, it, uuid)
        }
    }


    override suspend fun insertOrUpdatePatientPatchEntity(
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientFhirId,
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.PATCH
        ).let { patientPatchGenericEntity ->
            insertPatientGenericEntityPatch(patientPatchGenericEntity, patientFhirId, map, uuid)
        }
    }

    override suspend fun insertOrUpdateAppointmentPatch(
        appointmentFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            appointmentFhirId,
            GenericTypeEnum.APPOINTMENT,
            SyncType.PATCH
        ).let { appointmentGenericEntity ->
            insertOrUpdateAppointmentGenericEntityPatch(
                appointmentGenericEntity,
                map,
                appointmentFhirId,
                uuid
            )
        }
    }

    override suspend fun insertOrUpdatePhotoPrescriptionPatch(
        prescriptionFhirId: String,
        prescriptionPhotoPatch: PrescriptionPhotoPatch,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            prescriptionFhirId,
            GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
            SyncType.PATCH
        ).let { prescriptionGenericEntity ->
            insertOrUpdatePhotoPrescriptionGenericEntityPatch(
                prescriptionFhirId = prescriptionFhirId,
                prescriptionGenericEntity = prescriptionGenericEntity,
                prescriptionPhotoPatch = prescriptionPhotoPatch,
                uuid = uuid
            )
        }
    }

    override suspend fun insertOrUpdateCVDPatch(
        cvdFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.CVD,
            SyncType.PATCH
        ).let { genericEntities ->
            insertOrUpdateCVDGenericEntityPatch(genericEntities, cvdFhirId, map, uuid)
        }
    }
    override suspend fun insertOrUpdateSymDiagPatchEntity(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = fhirId,
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS,
            syncType = SyncType.PATCH
        ).let { genericEntity ->
            insertSymDiagGenericEntityPatch(genericEntity, fhirId, map, uuid)
        }
    }

    override suspend fun insertOrUpdateVitalPatchEntity(
        vitalFhirId: String, map: Map<String, Any>, uuid: String
    ): Long {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.VITAL,
            syncType = SyncType.PATCH
        ).let { genericEntity ->
            insertVitalGenericEntityPatch(genericEntity, vitalFhirId, map, uuid)
        }
    }
    override suspend fun insertPatientLastUpdated(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientLastUpdatedResponse.uuid,
            genericTypeEnum = GenericTypeEnum.LAST_UPDATED,
            syncType = SyncType.POST
        ).let { patientLastUpdatedGenericEntity ->
            insertPatientLastUpdatedGenericEntity(
                patientLastUpdatedResponse,
                patientLastUpdatedGenericEntity,
                uuid
            )
        }
    }
    
    override suspend fun removeGenericRecord(id: String): Int {
        return genericDao.removeGenericRecord(id)
    }

    override suspend fun insertDeleteRequest(
        fhirId: String,
        typeEnum: GenericTypeEnum,
        syncType: SyncType
    ): Long {
        return genericDao.insertGenericEntity(
            GenericEntity(
                id = UUIDBuilder.generateUUID(),
                patientId = fhirId,
                payload = fhirId.toJson(),
                type = typeEnum,
                syncType = syncType
            )
        )[0]
    }

    override suspend fun insertPhotoLabTestAndMedRecord(
        map: Map<String, Any>,
        patientId: String,
        uuid: String,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = map[LabTestAndMedConstants.PATIENT_ID].toString(),
            genericTypeEnum = typeEnum,
            syncType = SyncType.POST
        ).let { genericEntity ->
            insertLabTestPhotoGenericEntity(
                map = map,
                patientId = patientId,
                photoGenericEntity = genericEntity,
                uuid = uuid, typeEnum = typeEnum
            )
        }
    }
    
    override suspend fun insertOrUpdatePhotoLabTestAndMedPatch(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(
            fhirId,
            typeEnum,
            SyncType.PATCH
        ).let { prescriptionGenericEntity ->
            insertOrUpdatePhotoLabTestGenericEntityPatch(
                prescriptionFhirId = fhirId,
                prescriptionGenericEntity = prescriptionGenericEntity,
                map = map,
                uuid = uuid,
                typeEnum = typeEnum
            )
        }

    }
}