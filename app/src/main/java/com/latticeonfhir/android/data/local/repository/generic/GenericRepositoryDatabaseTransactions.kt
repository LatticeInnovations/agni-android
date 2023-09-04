package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.AppointmentPatchRequest
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
import com.latticeonfhir.android.utils.constants.Id
import com.latticeonfhir.android.utils.converters.responseconverter.FHIR.isFhirId
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson

open class GenericRepositoryDatabaseTransactions(
    private val genericDao: GenericDao,
    private val patientDao: PatientDao,
    private val scheduleDao: ScheduleDao,
    private val appointmentDao: AppointmentDao
) {

    protected suspend fun insertPatientGenericEntity(
        patientResponse: PatientResponse,
        patientGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (patientGenericEntity != null) {
            genericDao.insertGenericEntity(
                patientGenericEntity.copy(payload = patientResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientResponse.id,
                    payload = patientResponse.toJson(),
                    type = GenericTypeEnum.PATIENT,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertRelationGenericEntity(
        relationGenericEntity: GenericEntity?,
        relatedPersonResponse: RelatedPersonResponse,
        uuid: String,
        patientId: String
    ): Long {
        return relationGenericEntity?.payload?.fromJson<MutableMap<String, Any>>()?.mapToObject(
            RelatedPersonResponse::class.java
        )?.let { existingRelatedPersonResponse ->
            val updatedRelationList = existingRelatedPersonResponse.relationship.toMutableList()
                .apply { addAll(relatedPersonResponse.relationship) }
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = relationGenericEntity.id,
                    patientId = relationGenericEntity.patientId,
                    payload = existingRelatedPersonResponse.copy(relationship = updatedRelationList)
                        .toJson(),
                    type = GenericTypeEnum.RELATION,
                    syncType = SyncType.POST
                )
            )[0]
        } ?: genericDao.insertGenericEntity(
            GenericEntity(
                id = uuid,
                patientId = patientId,
                payload = relatedPersonResponse.toJson(),
                type = GenericTypeEnum.RELATION,
                syncType = SyncType.POST
            )
        )[0]
    }

    protected suspend fun updateRelationFhirIdInGenericEntity(relationGenericEntity: GenericEntity) {
        val existingMap = relationGenericEntity.payload.fromJson<MutableMap<String, Any>>().mapToObject(RelatedPersonResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                relationGenericEntity.copy(
                    payload = existingMap.copy(
                        id = if (existingMap.id.isFhirId()) existingMap.id else getPatientFhirIdById(
                            existingMap.id
                        )!!,
                        relationship = existingMap.relationship.map { relationship ->
                            relationship.copy(
                                relativeId = if (relationship.relativeId.isFhirId()) relationship.relativeId else getPatientFhirIdById(
                                    relationship.relativeId
                                )!!
                            )
                        }
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updatePrescriptionFhirIdInGenericEntity(prescriptionGenericEntity: GenericEntity) {
        val existingMap =
            prescriptionGenericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(PrescriptionResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                prescriptionGenericEntity.copy(
                    payload = existingMap.copy(
                        patientFhirId = if (!existingMap.patientFhirId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientFhirId
                        )!! else existingMap.patientFhirId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun insertScheduleGenericEntity(scheduleGenericEntity: GenericEntity?, scheduleResponse: ScheduleResponse, uuid: String): Long {
        return if (scheduleGenericEntity != null) {
            genericDao.insertGenericEntity(
                scheduleGenericEntity.copy(payload = scheduleResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = scheduleResponse.uuid,
                    payload = scheduleResponse.toJson(),
                    type = GenericTypeEnum.SCHEDULE,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertAppointmentGenericEntity(appointmentGenericEntity: GenericEntity?, appointmentResponse: AppointmentResponse, uuid: String): Long {
        return if (appointmentGenericEntity != null) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(payload = appointmentResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = appointmentResponse.uuid,
                    payload = appointmentResponse.toJson(),
                    type = GenericTypeEnum.APPOINTMENT,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity: GenericEntity) {
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>().mapToObject(AppointmentResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(
                    payload = existingMap.copy(
                        patientFhirId = if (!existingMap.patientFhirId!!.isFhirId()) getPatientFhirIdById(
                            existingMap.patientFhirId
                        )!! else existingMap.patientFhirId,
                        scheduleId = if (!existingMap.scheduleId.isFhirId()) getScheduleFhirIdById(
                            existingMap.scheduleId
                        )!! else existingMap.scheduleId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateAppointmentFhirIdInGenericEntityPatch(appointmentGenericEntity: GenericEntity) {
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>().mapToObject(AppointmentPatchRequest::class.java)
        if (existingMap?.scheduleId != null && !(existingMap.scheduleId.value as String).isFhirId()) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(
                    payload = existingMap.copy(
                        scheduleId = existingMap.scheduleId.copy(
                            value = getScheduleFhirIdById(existingMap.scheduleId.value)
                        )
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun insertOrUpdateAppointmentGenericEntityPatch(appointmentGenericEntity: GenericEntity?, map: Map<String, Any>, appointmentFhirId: String, uuid: String): Long {
        return if (appointmentGenericEntity != null) {
            val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            map.entries.forEach { mapEntry ->
                existingMap[mapEntry.key] = mapEntry.value
            }
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = appointmentGenericEntity.id,
                    patientId = appointmentFhirId,
                    payload = existingMap.toJson(),
                    type = GenericTypeEnum.APPOINTMENT,
                    syncType = SyncType.PATCH
                )
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = appointmentFhirId,
                    payload = map.toMutableMap().let { mutableMap ->
                        mutableMap[Id.APPOINTMENT_ID] = appointmentFhirId
                        mutableMap
                    }.toJson(),
                    type = GenericTypeEnum.APPOINTMENT,
                    syncType = SyncType.PATCH
                )
            )[0]
        }
    }

    private suspend fun getPatientFhirIdById(patientId: String): String? {
        return patientDao.getPatientDataById(patientId)[0].patientEntity.fhirId
    }

    private suspend fun getScheduleFhirIdById(scheduleId: String): String? {
        return scheduleDao.getScheduleById(scheduleId)[0].scheduleFhirId
    }

    private suspend fun getAppointmentFhirIdById(appointmentId: String): String? {
        return appointmentDao.getAppointmentById(appointmentId)[0].appointmentFhirId
    }
}