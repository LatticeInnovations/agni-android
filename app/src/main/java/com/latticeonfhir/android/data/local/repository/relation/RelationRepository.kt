package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship

interface RelationRepository {

    suspend fun addRelation(relationship: Relationship): List<Long>
    suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long>
    suspend fun getRelationBetween(fromId: String, toId: String): RelationEnum
    suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity>
    suspend fun deleteRelation(vararg relationId: String): Int
}