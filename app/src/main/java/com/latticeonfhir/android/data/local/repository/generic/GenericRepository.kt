package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

interface GenericRepository {

    suspend fun insertPostObjectEntity(patientId: String, entity: Any, typeEnum: GenericTypeEnum): Long
    suspend fun insertOrUpdateGenericObjectEntity(patientId: String,map: Map<String,ChangeRequest>, typeEnum: GenericTypeEnum): Long
    suspend fun insertOrUpdateGenericArrayEntity(patientId: String, map: Map<String, List<ChangeRequest>>, typeEnum: GenericTypeEnum): Long
}