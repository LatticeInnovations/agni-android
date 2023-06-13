package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.utils.builders.GenericEntity.processPatch
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.constants.Id.ID
import com.latticeonfhir.android.utils.constants.RelationConstants.RELATIONSHIP
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class GenericRepositoryImpl @Inject constructor(private val genericDao: GenericDao) :
    GenericRepository {

    override suspend fun insertOrUpdatePostEntity(
        patientId: String,
        entity: Any,
        typeEnum: GenericTypeEnum,
        replaceEntireRow: Boolean
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
                        id = UUIDBuilder.generateUUID(),
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
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientFhirId, typeEnum, SyncType.PATCH).run {
            if (this != null) {
                val existingMap = payload.fromJson<MutableMap<String, Any>>()
                if(existingMap[ID] == null) {
                    existingMap[ID] = patientFhirId
                }
                map.entries.forEach { mapEntry ->
                    if ((mapEntry.value is List<*>)) {
                        existingMap[mapEntry.key] = processPatch(
                            existingMap,
                            mapEntry,
                            (mapEntry.value as List<ChangeRequest>)
                        )
                    } else {
                       processPatch(existingMap, mapEntry)
                    }
                }
                if (existingMap.size == 1) {
                    genericDao.deleteSyncPayload(listOf(id)).toLong()
                } else {
                    genericDao.insertGenericEntity(
                        copy(payload = existingMap.toJson())
                    )[0]
                }
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = UUIDBuilder.generateUUID(),
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

    override suspend fun getNonSyncedPostRelations(): List<GenericEntity> {
        return genericDao.getNotSyncedPostRelation()
    }
}