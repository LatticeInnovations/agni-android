package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity

interface RelationRepository {

    suspend fun addRelation(relationEntity: RelationEntity): List<Long>
    suspend fun listOfRelation(listOfRelations: List<RelationEntity>): List<Long>
}