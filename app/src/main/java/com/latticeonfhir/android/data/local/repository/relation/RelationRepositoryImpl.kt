package com.latticeonfhir.android.data.local.repository.relation

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toReverseRelation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RelationRepositoryImpl @Inject constructor(
    private val relationDao: RelationDao,
    private val patientDao: PatientDao
) : RelationRepository {

    override suspend fun addRelation(relationship: Relationship): List<Long> {
        return relationDao.insertRelation(
            relationship.toRelationEntity()
        ).also {
            relationship.toRelationEntity().toReverseRelation(patientDao) { relationEntity ->
                CoroutineScope(Dispatchers.IO).launch {
                    relationDao.insertRelation(
                        relationEntity
                    )
                }
            }
        }
    }

    override suspend fun addListOfRelation(listOfRelations: List<RelationEntity>): List<Long> {
        return relationDao.insertRelation(
            *listOfRelations.toTypedArray()
        )
    }

    override suspend fun getRelationBetween(fromId: String, toId: String): RelationEnum {
        return relationDao.getRelation(fromId, toId)
    }

    override suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity> {
        return relationDao.getAllRelationOfPatient(patientId)
    }

    override suspend fun deleteRelation(vararg relationId: String): Int {
        return relationDao.deleteRelation(*relationId)
    }
}