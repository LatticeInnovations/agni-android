package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

interface GenericRepository {

    suspend fun insertGenericEntity(patientId: String,map: Map<String, ChangeRequest>, typeEnum: GenericTypeEnum): Long
}