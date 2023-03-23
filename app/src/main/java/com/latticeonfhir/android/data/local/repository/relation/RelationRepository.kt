package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity

interface RelationRepository {

    suspend fun addRelation(relationEntity: RelationEntity): List<Long>
    suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long>
    suspend fun getRelationBetween(fromId: String, toId: String): RelationEnum
    suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity>
}