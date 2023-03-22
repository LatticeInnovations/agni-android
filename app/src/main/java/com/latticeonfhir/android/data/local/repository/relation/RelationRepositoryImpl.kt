package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import javax.inject.Inject

class RelationRepositoryImpl @Inject constructor(private val relationDao: RelationDao) : RelationRepository {

    override suspend fun addRelation(relationEntity: RelationEntity): List<Long> {
        return relationDao.insertRelation(
            relationEntity
        )
    }

    override suspend fun listOfRelation(listOfRelations: List<RelationEntity>): List<Long> {
        return relationDao.insertRelation(
            *listOfRelations.toTypedArray()
        )
    }
}