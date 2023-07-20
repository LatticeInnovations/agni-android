package com.latticeonfhir.android.data.local.repository.generic

import android.content.Context
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.builders.GenericEntity.processPatch
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.constants.Id.ID
import com.latticeonfhir.android.utils.constants.RelationConstants.RELATIONSHIP
import com.latticeonfhir.android.utils.converters.responseconverter.FHIR.isFhirId
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class GenericRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val genericDao: GenericDao,
    private val patientDao: PatientDao
) : GenericRepository {

    private val workRequestBuilders: WorkRequestBuilders by lazy { WorkRequestBuilders(context,this) }

    override suspend fun insertPatient(patientResponse: PatientResponse): Long {
        return genericDao.getGenericEntityById(
            patientId = patientResponse.id,
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.POST
        ).let { patientGenericEntity ->
            if (patientGenericEntity != null) {
                genericDao.insertGenericEntity(
                    patientGenericEntity.copy(payload = patientResponse.toJson())
                )[0]
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = UUIDBuilder.generateUUID(),
                        patientId = patientResponse.id,
                        payload = patientResponse.toJson(),
                        type = GenericTypeEnum.PATIENT,
                        syncType = SyncType.POST
                    )
                )[0]
            }
        }.also {
            CoroutineScope(Dispatchers.IO).launch {
                workRequestBuilders.apply {
                    uploadPatientWorker { errorReceived, errorMsg ->}

                    setPatientPatchWorker { errorReceived, errorMsg ->  }

                    setRelationPatchWorker { errorReceived, errorMsg ->}
                }
            }
        }
    }

    override suspend fun insertRelation(
        patientId: String,
        relatedPersonResponse: RelatedPersonResponse
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientId,
            genericTypeEnum = GenericTypeEnum.RELATION,
            syncType = SyncType.POST
        ).let { relationGenericEntity ->
            relationGenericEntity?.payload?.fromJson<MutableMap<String, Any>>()
                ?.mapToObject(RelatedPersonResponse::class.java)
                ?.let { existingRelatedPersonResponse ->
                    val updatedRelationList =
                        existingRelatedPersonResponse.relationship.toMutableList().apply {
                            addAll(relatedPersonResponse.relationship)
                        }
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
                    id = UUIDBuilder.generateUUID(),
                    patientId = patientId,
                    payload = relatedPersonResponse.toJson(),
                    type = GenericTypeEnum.RELATION,
                    syncType = SyncType.POST
                )
            )[0]
        }.also {
            CoroutineScope(Dispatchers.IO).launch {
                workRequestBuilders.apply {
                    uploadPatientWorker { errorReceived, errorMsg ->}

                    setPatientPatchWorker { errorReceived, errorMsg ->  }

                    setRelationPatchWorker { errorReceived, errorMsg ->}
                }
            }
        }
    }

    override suspend fun updateRelationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.RELATION).forEach { relationGenericEntity ->
            val existingMap = relationGenericEntity.payload.fromJson<MutableMap<String, Any>>().mapToObject(RelatedPersonResponse::class.java)
            if (existingMap != null) {
                genericDao.insertGenericEntity(
                    relationGenericEntity.copy(
                        payload = existingMap.copy(
                            id = if (existingMap.id.isFhirId()) existingMap.id else getPatientFhirIdById(existingMap.id)!!,
                            relationship = existingMap.relationship.map { relationship ->
                                relationship.copy(
                                    relativeId = if (relationship.relativeId.isFhirId()) relationship.relativeId else getPatientFhirIdById(relationship.relativeId)!!
                                )
                            }
                        ).toJson()
                    )
                )
            }
        }
    }

    override suspend fun insertPrescription(
        prescriptionResponse: PrescriptionResponse
    ): Long {
        return genericDao.insertGenericEntity(
            GenericEntity(
                id = UUIDBuilder.generateUUID(),
                patientId = prescriptionResponse.patientFhirId,
                payload = prescriptionResponse.toJson(),
                type = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            )
        )[0].also {
            CoroutineScope(Dispatchers.IO).launch {
                workRequestBuilders.apply {
                    uploadPatientWorker { errorReceived, errorMsg ->}

                    setPatientPatchWorker { errorReceived, errorMsg ->  }

                    setRelationPatchWorker { errorReceived, errorMsg ->}
                }
            }
        }
    }

    override suspend fun updatePrescriptionFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION).forEach { prescriptionGenericEntity ->
            val existingMap = prescriptionGenericEntity.payload.fromJson<MutableMap<String, Any>>().mapToObject(PrescriptionResponse::class.java)
            if (existingMap != null && !existingMap.patientFhirId.isFhirId()) {
                genericDao.insertGenericEntity(
                    prescriptionGenericEntity.copy(
                        payload = existingMap.copy(
                            patientFhirId = getPatientFhirIdById(existingMap.patientFhirId)!!
                        ).toJson()
                    )
                )
            }
        }
    }

    @Deprecated("This method was deprecated use above methods to store POST Generic Entity")
    override suspend fun insertOrUpdatePostEntity(
        patientId: String,
        entity: Any,
        typeEnum: GenericTypeEnum,
        replaceEntireRow: Boolean,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum, SyncType.POST).run {
            if (this != null && typeEnum != GenericTypeEnum.PRESCRIPTION) {
                if (typeEnum == GenericTypeEnum.RELATION && !replaceEntireRow) {
                    val existingMap = payload.fromJson<MutableMap<String, Any>>()
                    val list = existingMap[RELATIONSHIP] as MutableList<Relationship>
                    val newMap = entity as RelatedPersonResponse
                    list.add(newMap.relationship[0])
                    existingMap[RELATIONSHIP] = list
                    genericDao.insertGenericEntity(copy(payload = existingMap.toJson()))[0]
                } else {
                    genericDao.insertGenericEntity(copy(payload = entity.toJson()))[0]
                }
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = uuid,
                        patientId = patientId,
                        payload = entity.toJson(),
                        type = typeEnum,
                        syncType = SyncType.POST
                    )
                )[0]
            }
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
        }.also {
            CoroutineScope(Dispatchers.IO).launch {
                workRequestBuilders.apply {
                    uploadPatientWorker { errorReceived, errorMsg ->}

                    setPatientPatchWorker { errorReceived, errorMsg ->  }

                    setRelationPatchWorker { errorReceived, errorMsg ->}
                }
            }
        }
    }

    private suspend fun getPatientFhirIdById(patientId: String): String? {
        return patientDao.getPatientDataById(patientId)[0].patientEntity.fhirId
    }
}