package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

interface GenericRepository {

    suspend fun insertGenericEntity(id: String,map: Map<String, ChangeRequest>): Long
}