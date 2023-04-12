package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.model.RelationBetween
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView

interface RelationRepository {

    suspend fun addRelation(relation: Relation): List<Long>
    suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long>
    suspend fun getRelationBetween(fromId: String, toId: String): List<RelationView>
    suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity>
    suspend fun deleteRelation(vararg relationId: String): Int
    suspend fun deleteRelation(fromId: String, toId: String): Int
    suspend fun deleteAllRelationOfPatient(patientId: String): Int
}