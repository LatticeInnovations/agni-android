package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

class GenericRepositoryImpl @Inject constructor(private val genericDao: GenericDao) :
    GenericRepository {

    override suspend fun insertGenericEntity(
        patientId: String,
        map: Map<String, ChangeRequest>,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(patientId, typeEnum).run {
            if (this != null) {
                val existingMap = this.payload.fromJson<MutableMap<String, ChangeRequest>>()
                map.entries.forEach {
                    existingMap[it.key] = it.value
                }
                genericDao.insertChangeRequest(
                    this.copy(payload = existingMap.toJson())
                )
            } else {
                genericDao.insertChangeRequest(
                    GenericEntity(
                        id = UUIDBuilder.generateUUID(),
                        patientId = patientId,
                        payload = map.toJson(),
                        type = typeEnum
                    )
                )
            }
        }
    }
}