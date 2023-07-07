package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder

interface GenericRepository {

    suspend fun insertOrUpdatePostEntity(patientId: String, entity: Any, typeEnum: GenericTypeEnum, replaceEntireRow: Boolean = false, uuid: String = UUIDBuilder.generateUUID()): Long
    suspend fun insertOrUpdatePatchEntity(patientFhirId: String,map: Map<String,Any>, typeEnum: GenericTypeEnum, uuid: String = UUIDBuilder.generateUUID()): Long

    suspend fun getNonSyncedPostRelations(): List<GenericEntity>
    suspend fun getNonSyncedPostPrescriptions(): List<GenericEntity>
}