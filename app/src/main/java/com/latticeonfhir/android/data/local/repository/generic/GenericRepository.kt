package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity

interface GenericRepository {

    suspend fun insertOrUpdatePostEntity(patientId: String, entity: Any, typeEnum: GenericTypeEnum, replaceEntireRow: Boolean = false): Long
    suspend fun insertOrUpdatePatchEntity(patientFhirId: String,map: Map<String,Any>, typeEnum: GenericTypeEnum): Long

    suspend fun getNonSyncedPostRelations(): List<GenericEntity>
}