package com.heartcare.agni.data.local.repository.relation

import com.heartcare.agni.data.local.model.relation.Relation
import com.heartcare.agni.data.local.roomdb.entities.relation.RelationEntity
import com.heartcare.agni.data.local.roomdb.views.RelationView

interface RelationRepository {

    suspend fun addRelation(relation: Relation, relationAdded: (List<Long>) -> Unit)
    suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long>
    suspend fun updateRelation(relation: Relation, relationUpdated: (Int) -> Unit)
    suspend fun getRelationBetween(fromId: String, toId: String): List<RelationView>
    suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity>
    suspend fun deleteRelation(vararg relationId: String): Int
    suspend fun deleteRelation(fromId: String, toId: String): Int
    suspend fun deleteAllRelationOfPatient(patientId: String): Int
}