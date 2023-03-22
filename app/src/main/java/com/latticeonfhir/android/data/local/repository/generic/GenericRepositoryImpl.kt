package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

class GenericRepositoryImpl @Inject constructor(private val genericDao: GenericDao) :
    GenericRepository {

    override suspend fun insertPostObjectEntity(
        patientId: String,
        entity: Any,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum, SyncType.POST).run {
            if (this != null) {
                genericDao.insertGenericEntity(copy(payload = entity.toJson()))
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        UUIDBuilder.generateUUID(),
                        patientId,
                        entity.toJson(),
                        typeEnum,
                        SyncType.POST
                    )
                )
            }
        }
    }

    override suspend fun insertOrUpdateGenericObjectEntity(
        patientId: String,
        map: Map<String, ChangeRequest>,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum, SyncType.PATCH).run {
            if (this != null) {
                val existingMap = payload.fromJson<MutableMap<String, ChangeRequest>>()
                map.entries.forEach {
                    existingMap[it.key] = it.value
                }
                genericDao.insertGenericEntity(
                    copy(payload = existingMap.toJson())
                )
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = UUIDBuilder.generateUUID(),
                        patientId = patientId,
                        payload = map.toJson(),
                        type = typeEnum,
                        syncType = SyncType.PATCH
                    )
                )
            }
        }
    }

    override suspend fun insertOrUpdateGenericArrayEntity(
        patientId: String,
        map: Map<String, List<ChangeRequest>>,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum, SyncType.PATCH).run {
            if (this != null) {
                val existingMap = payload.fromJson<Map<String, MutableList<ChangeRequest>>>()
                existingMap.keys.forEach { key ->
                    map[key]?.let { value -> existingMap[key]?.addAll(value) }
                }
                genericDao.insertGenericEntity(
                    copy(payload = existingMap.toJson())
                )
            } else {
                genericDao.insertGenericEntity(
                    GenericEntity(
                        id = UUIDBuilder.generateUUID(),
                        patientId = patientId,
                        payload = map.toJson(),
                        type = typeEnum,
                        syncType = SyncType.PATCH
                    )
                )
            }
        }
    }
}