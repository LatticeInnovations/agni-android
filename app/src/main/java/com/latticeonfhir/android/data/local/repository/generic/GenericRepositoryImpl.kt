package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.utils.builders.GenericEntity.processPatch
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

class GenericRepositoryImpl @Inject constructor(private val genericDao: GenericDao) :
    GenericRepository {

    override suspend fun insertOrUpdatePostEntity(
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
                        id = UUIDBuilder.generateUUID(),
                        patientId = patientId,
                        payload = entity.toJson(),
                        type = typeEnum,
                        syncType = SyncType.POST
                    )
                )
            }
        }
    }

    override suspend fun insertOrUpdatePatchEntity(
        patientId: String,
        map: Map<String, Any>,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum, SyncType.PATCH).run {
            if (this != null) {
                val existingMap = payload.fromJson<MutableMap<String, Any>>()
                map.entries.forEach { mapEntry ->
                    if ((mapEntry.value is List<*>)) {
                        existingMap[mapEntry.key] = processPatch(
                            existingMap,
                            mapEntry,
                            (mapEntry.value as List<ChangeRequest>)
                        )
                    } else {
                        existingMap[mapEntry.key] = mapEntry.value
                    }
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