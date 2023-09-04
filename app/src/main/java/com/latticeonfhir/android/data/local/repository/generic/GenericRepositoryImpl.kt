package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.AppointmentPatchRequest
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.GenericEntity.processPatch
import com.latticeonfhir.android.utils.constants.Id
import com.latticeonfhir.android.utils.constants.Id.APPOINTMENT_ID
import com.latticeonfhir.android.utils.constants.Id.ID
import com.latticeonfhir.android.utils.converters.responseconverter.FHIR.isFhirId
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

/**
 *
 * Here we are passing UUID in Parameters due to Unit Testing Scenario.
 * if we generate UUID in repo Unit tests were failing.
 * Do not pass uuid from anywhere else it will automatically generate here.
 *
 */
@Suppress("UNCHECKED_CAST")
class GenericRepositoryImpl @Inject constructor(
    private val genericDao: GenericDao,
    patientDao: PatientDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao
) : GenericRepository, GenericRepositoryDatabaseTransactions(genericDao,patientDao, scheduleDao, appointmentDao) {

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
            insertRelationGenericEntity(relationGenericEntity, relatedPersonResponse, uuid, patientId)
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
                patientId = prescriptionResponse.patientFhirId,
                payload = prescriptionResponse.toJson(),
                type = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            )
        )[0]
    }

    override suspend fun updatePrescriptionFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION).forEach { prescriptionGenericEntity ->
            updatePrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
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
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT).forEach { appointmentGenericEntity ->
            updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity)
        }
    }

    override suspend fun updateAppointmentFhirIdInPatch() {
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT, SyncType.PATCH).forEach { appointmentGenericEntity ->
            updateAppointmentFhirIdInGenericEntityPatch(appointmentGenericEntity)
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

    override suspend fun insertOrUpdatePatchEntity(
        patientFhirId: String,
        map: Map<String, Any>,
        typeEnum: GenericTypeEnum,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(patientFhirId, typeEnum, SyncType.PATCH).run {
            if (this != null) {
                /** Data with this record already present */
                val existingMap = payload.fromJson<MutableMap<String, Any>>()
                if (existingMap[ID] == null) {
                    existingMap[ID] = patientFhirId
                }
                map.entries.forEach { mapEntry ->
                    if ((mapEntry.value is List<*>)) {
                        /** Get Processed Data for List Change Request */
                        val processPatchData = processPatch(
                            existingMap,
                            mapEntry,
                            (mapEntry.value as List<ChangeRequest>)
                        )

                        /** Check for data is empty */
                        if (processPatchData.isNotEmpty()) {
                            existingMap[mapEntry.key] = processPatchData
                        } else {
                            /** If empty remove that key from map */
                            existingMap.remove(mapEntry.key)
                        }
                    } else {
                        processPatch(existingMap, mapEntry)
                    }
                }
                /** It denotes only ID key is present in map */
                if (existingMap.size == 1) {
                    genericDao.deleteSyncPayload(listOf(id)).toLong()
                } else {
                    /** Insert Updated Map */
                    genericDao.insertGenericEntity(
                        copy(payload = existingMap.toJson())
                    )[0]
                }
            } else {
                /** Insert Freshly Patch data */
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = uuid,
                        patientId = patientFhirId,
                        payload = map.toMutableMap().let { mutableMap ->
                            mutableMap[ID] = patientFhirId
                            mutableMap
                        }.toJson(),
                        type = typeEnum,
                        syncType = SyncType.PATCH
                    )
                )[0]
            }
        }
    }

    override suspend fun insertOrUpdatePatientPatch(
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(patientFhirId,GenericTypeEnum.PATIENT,SyncType.PATCH).run {
            if(this != null) {
                map.entries.forEach { entry ->
                    if(entry.value is List<*>) {

                    } else {

                    }
                }
                0
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = uuid,
                        patientId = patientFhirId,
                        payload = map.toMutableMap().let { mutableMap ->
                            mutableMap[ID] = patientFhirId
                            mutableMap
                        }.toJson(),
                        type = GenericTypeEnum.PATIENT,
                        syncType = SyncType.PATCH
                    )
                )[0]
            }
        }
    }

    override suspend fun insertOrUpdateRelationPatch(
        patientId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        TODO("Not yet implemented")
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
            insertOrUpdateAppointmentGenericEntityPatch(appointmentGenericEntity, map, appointmentFhirId, uuid)
        }
    }
}