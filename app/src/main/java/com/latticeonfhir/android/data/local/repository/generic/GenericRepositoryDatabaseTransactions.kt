package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.AppointmentPatchRequest
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.GenericEntityPatchBuilder.processPatch
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
        val existingMap = relationGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(RelatedPersonResponse::class.java)
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

    protected suspend fun updateFormPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity: GenericEntity) {
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

    protected suspend fun updatePhotoPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity: GenericEntity) {
        val existingMap =
            prescriptionGenericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(PrescriptionPhotoResponse::class.java)
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

    protected suspend fun insertScheduleGenericEntity(
        scheduleGenericEntity: GenericEntity?,
        scheduleResponse: ScheduleResponse,
        uuid: String
    ): Long {
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

    protected suspend fun insertAppointmentGenericEntity(
        appointmentGenericEntity: GenericEntity?,
        appointmentResponse: AppointmentResponse,
        uuid: String
    ): Long {
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

    protected suspend fun insertCVDGenericEntity(
        cvdGenericEntity: GenericEntity?,
        cvdResponse: CVDResponse,
        uuid: String
    ): Long {
        return if (cvdGenericEntity != null) {
            genericDao.insertGenericEntity(
                cvdGenericEntity.copy(payload = cvdResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = cvdResponse.cvdUuid,
                    payload = cvdResponse.toJson(),
                    type = GenericTypeEnum.CVD,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertVitalGenericEntity(
        vitalLocal: VitalLocal,
        genericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (genericEntity != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(payload = vitalLocal.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid, patientId = vitalLocal.vitalUuid,
                    payload = vitalLocal.toJson(),
                    type = GenericTypeEnum.VITAL,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity: GenericEntity) {
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(AppointmentResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(
                    payload = existingMap.copy(
                        patientFhirId = if (!existingMap.patientFhirId.isFhirId()) getPatientFhirIdById(
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
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(AppointmentPatchRequest::class.java)
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

    protected suspend fun updateCVDFhirIdInGenericEntity(cvdGenericEntity: GenericEntity) {
        val existingMap = cvdGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(CVDResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                cvdGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateVitalFhirIdInGenericEntity(genericEntity: GenericEntity) {
        val existingMap =
            genericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(VitalLocal::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId!!.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun insertOrUpdateAppointmentGenericEntityPatch(
        appointmentGenericEntity: GenericEntity?,
        map: Map<String, Any>,
        appointmentFhirId: String,
        uuid: String
    ): Long {
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

    protected suspend fun insertOrUpdatePhotoPrescriptionGenericEntityPatch(
        prescriptionGenericEntity: GenericEntity?,
        prescriptionPhotoResponse: PrescriptionPhotoResponse,
        prescriptionFhirId: String,
        uuid: String
    ): Long {
        return if (prescriptionGenericEntity != null) {
            genericDao.insertGenericEntity(
                prescriptionGenericEntity.copy(payload = prescriptionPhotoResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = prescriptionFhirId,
                    payload = prescriptionPhotoResponse.toJson(),
                    type = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
                    syncType = SyncType.PATCH
                )
            )[0]
        }
    }


    protected suspend fun insertOrUpdateCVDGenericEntityPatch(
        genericEntity: List<GenericEntity>,
        cvdFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        var recordUpdated = false
        var lastInsertedId: Long = 0

        // Loop through existing records
        genericEntity.forEach { entity ->
            val existingMap = entity.payload.fromJson<MutableMap<String, Any>>()

            // Check if the "cvdFhirId" and "key" in the existing map match the new map
            if (existingMap["cvdFhirId"] == map["cvdFhirId"] && existingMap["key"] == map["key"]) {

                // If it matches, update the existing map's component with new values, except for "operation"
                val existingComponent =
                    existingMap["component"] as? MutableMap<String, Any> ?: mutableMapOf()

                map["component"]?.let { newComponent ->
                    if (newComponent is Map<*, *>) {
                        newComponent.forEach { (key, value) ->
                            if (key == "operation" && existingComponent["operation"] == "add") {
                                // Skip updating "operation" if it's already "add"
                                return@forEach
                            }
                            // Update other component keys
                            existingComponent[key as String] = value as Any
                        }
                    }
                }

                // Update the existing map with the modified component
                existingMap["component"] = existingComponent

                // Update the existing record in the DB
                lastInsertedId = genericDao.insertGenericEntity(
                    entity.copy(payload = existingMap.toJson())
                )[0]
                recordUpdated = true  // Mark that an update has occurred
            }
        }

        // If no record was updated (i.e., "Height" was not found), insert a new record
        if (!recordUpdated) {
            lastInsertedId = genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = cvdFhirId,
                    payload = map.toJson(),
                    type = GenericTypeEnum.CVD,
                    syncType = SyncType.PATCH
                )
            )[0]
        }

        return lastInsertedId
    }

    protected suspend fun insertVitalGenericEntityPatch(
        genericEntity: List<GenericEntity>,
        vitalFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        var recordUpdated = false
        var lastInsertedId: Long = 0

        // Loop through existing records
        genericEntity.forEach { entity ->
            val existingMap = entity.payload.fromJson<MutableMap<String, Any>>()

            // Check if the "vitalFhirId" and "key" in the existing map match the new map
            if (existingMap["vitalFhirId"] == map["vitalFhirId"] && existingMap["key"] == map["key"]) {

                // If it matches, update the existing map's component with new values, except for "operation"
                val existingComponent =
                    existingMap["component"] as? MutableMap<String, Any> ?: mutableMapOf()

                map["component"]?.let { newComponent ->
                    if (newComponent is Map<*, *>) {
                        newComponent.forEach { (key, value) ->
                            if (key == "operation" && existingComponent["operation"] == "add") {
                                // Skip updating "operation" if it's already "add"
                                return@forEach
                            }
                            // Update other component keys
                            existingComponent[key as String] = value as Any
                        }
                    }
                }

                // Update the existing map with the modified component
                existingMap["component"] = existingComponent

                // Update the existing record in the DB
                lastInsertedId = genericDao.insertGenericEntity(
                    entity.copy(payload = existingMap.toJson())
                )[0]
                recordUpdated = true  // Mark that an update has occurred
            }
        }

        // If no record was updated (i.e., "Height" was not found), insert a new record
        if (!recordUpdated) {
            lastInsertedId = genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = vitalFhirId,
                    payload = map.toJson(),
                    type = GenericTypeEnum.VITAL,
                    syncType = SyncType.PATCH
                )
            )[0]
        }

        return lastInsertedId
    }


    private fun processPatientPatch(
        mapEntry: Map.Entry<String, Any>,
        existingMap: MutableMap<String, Any>
    ) {
        if (mapEntry.value is List<*>) {
            /** Get Processed Data for List Change Request */
            val processPatchData = processPatch(
                existingMap,
                mapEntry,
                ((mapEntry.value as List<*>).filterIsInstance<ChangeRequest>())
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

    protected suspend fun insertPatientGenericEntityPatch(
        patientGenericPatchEntity: GenericEntity?,
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return if (patientGenericPatchEntity != null) {
            /** Data with this record already present */
            val existingMap = patientGenericPatchEntity.payload.fromJson<MutableMap<String, Any>>()
            map.entries.forEach { mapEntry ->
                processPatientPatch(mapEntry, existingMap)
            }
            /** It denotes only ID key is present in map */
            if (existingMap.size == 1) {
                genericDao.deleteSyncPayload(listOf(patientGenericPatchEntity.id)).toLong()
            } else {
                /** Insert Updated Map */
                genericDao.insertGenericEntity(patientGenericPatchEntity.copy(payload = existingMap.toJson()))[0]
            }
        } else {
            /** Insert Freshly Patch data */
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientFhirId,
                    payload = map.toMutableMap().let { mutableMap ->
                        mutableMap[Id.ID] = patientFhirId
                        mutableMap
                    }.toJson(),
                    type = GenericTypeEnum.PATIENT,
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

    protected suspend fun insertPatientLastUpdatedGenericEntity(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        patientLastUpdatedGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (patientLastUpdatedGenericEntity != null) {
            genericDao.insertGenericEntity(
                patientLastUpdatedGenericEntity.copy(payload = patientLastUpdatedResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientLastUpdatedResponse.uuid,
                    payload = patientLastUpdatedResponse.toJson(),
                    type = GenericTypeEnum.LAST_UPDATED,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }


    protected suspend fun insertPrescriptionPhotoGenericEntity(
        prescriptionPhotoResponse: PrescriptionPhotoResponse,
        prescriptionPhotoGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (prescriptionPhotoGenericEntity != null) {
            genericDao.insertGenericEntity(
                prescriptionPhotoGenericEntity.copy(payload = prescriptionPhotoResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = prescriptionPhotoResponse.prescriptionId,
                    payload = prescriptionPhotoResponse.toJson(),
                    type = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }
}