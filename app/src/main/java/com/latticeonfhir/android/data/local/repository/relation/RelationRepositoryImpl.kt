package com.latticeonfhir.android.data.local.repository.relation

import androidx.lifecycle.LiveData
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.model.RelationBetween
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toReverseRelation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RelationRepositoryImpl @Inject constructor(
    private val relationDao: RelationDao,
    private val patientDao: PatientDao
) : RelationRepository {

    override suspend fun addRelation(relation: Relation): List<Long> {
        return relationDao.insertRelation(
            relation.toRelationEntity()
        ).also {
            relation.toRelationEntity().toReverseRelation(patientDao) { relationEntity ->
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

    override suspend fun updateRelation(relation: Relation, relationUpdated: (Int) -> Unit) {
        relationDao.updateRelation(
            relationEnum = RelationEnum.fromString(relation.relation),
            fromId = relation.patientId,
            toId = relation.relativeId
        ).also {
            relation.toRelationEntity().toReverseRelation(patientDao) { relationEntity ->
                CoroutineScope(Dispatchers.IO).launch {
                    relationUpdated(
                        relationDao.updateRelation(
                            relationEnum = relationEntity.relation,
                            fromId = relationEntity.fromId,
                            toId = relationEntity.toId
                        )
                    )
                }
            }
        }
    }

    override suspend fun getRelationBetween(fromId: String, toId: String): List<RelationView> {
        return relationDao.getRelation(fromId, toId)
    }

    override suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity> {
        return relationDao.getAllRelationOfPatient(patientId)
    }

    override suspend fun deleteRelation(vararg relationId: String): Int {
        return relationDao.deleteRelation(*relationId)
    }

    override suspend fun deleteRelation(fromId: String, toId: String): Int {
        return relationDao.deleteRelation(fromId, toId)
    }

    override suspend fun deleteAllRelationOfPatient(patientId: String): Int {
        return relationDao.deleteAllRelationOfPatient(patientId)
    }
}