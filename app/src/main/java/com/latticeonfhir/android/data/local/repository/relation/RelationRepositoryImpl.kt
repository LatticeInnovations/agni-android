package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import javax.inject.Inject

class RelationRepositoryImpl @Inject constructor(private val relationDao: RelationDao) : RelationRepository {

    override suspend fun addRelation(relationEntity: RelationEntity): List<Long> {
        return relationDao.insertRelation(
            relationEntity
        )
    }

    override suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long> {
        return relationDao.insertRelation(
            *listOfRelations.toTypedArray()
        )
    }

    override suspend fun getRelationBetween(fromId: String, toId: String): RelationEnum {
        return relationDao.getRelation(fromId,toId)
    }

    override suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity> {
        return relationDao.getAllRelationOfPatient(patientId)
    }
}