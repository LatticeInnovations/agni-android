package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.repository.relation.RelationRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RelationRepositoryTest: BaseClass() {
    @Mock
    lateinit var relationDao: RelationDao
    @Mock
    lateinit var patientDao: PatientDao
    lateinit var relationRepositoryImpl: RelationRepositoryImpl

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        relationRepositoryImpl = RelationRepositoryImpl(relationDao, patientDao)
    }

    val relation = Relation(id, relativeId, relationSpouse.value)

    @Test
    fun addRelationTest() = runBlocking {
        `when`(relationDao.insertRelation(relationEntity)).thenReturn(listOf(-1))

        relationRepositoryImpl.addRelation(relation){
            Assert.assertEquals(listOf<Long>(-1), it)
        }
    }

    @Test
    fun addListOfRelationTest() = runBlocking {
        `when`(relationDao.insertRelation(relationEntity)).thenReturn(listOf(-1))

        val result = relationRepositoryImpl.addListOfRelation(listOf(relationEntity))
        Assert.assertEquals(listOf<Long>(-1), result)
    }

    @Test
    fun updateRelationTest() = runBlocking {
        `when`(relationDao.updateRelation(relationSpouse, id, relativeId)).thenReturn(1)
        relationRepositoryImpl.updateRelation(relation){
            Assert.assertEquals(1, it)
        }
    }

    @Test
    fun getRelationBetweenTest() = runBlocking {
        val relationView1 = RelationView(
            id = UUIDBuilder.generateUUID(),
            patientFirstName = patientResponse.firstName,
            patientMiddleName = patientResponse.middleName,
            patientLastName = patientResponse.lastName,
            patientId = patientResponse.id,
            patientGender = patientResponse.gender,
            relation = relationSpouse,
            relativeId = relative.id,
            relativeFirstName = relative.firstName,
            relativeMiddleName = relative.middleName,
            relativeLastName = relative.lastName
        )
        val relationView2 = RelationView(
            id = UUIDBuilder.generateUUID(),
            patientFirstName = relative.firstName,
            patientMiddleName = patientResponse.middleName,
            patientLastName = relative.lastName,
            patientId = relative.id,
            patientGender = relative.gender,
            relation = relationSpouse,
            relativeId = patientResponse.id,
            relativeFirstName = patientResponse.firstName,
            relativeMiddleName = patientResponse.middleName,
            relativeLastName = patientResponse.lastName
        )
        `when`(relationDao.getRelation(id, relativeId)).thenReturn(listOf(relationView1, relationView2))

        val actual = relationRepositoryImpl.getRelationBetween(id, relativeId)
        Assert.assertEquals(listOf(relationView1, relationView2), actual)
    }

    @Test
    fun getAllRelationOfPatientTest() = runBlocking {
        `when`(relationDao.getAllRelationOfPatient(id)).thenReturn(listOf(relationEntity))
        val actual = relationRepositoryImpl.getAllRelationOfPatient(id)
        Assert.assertEquals(listOf(relationEntity), actual)
    }

    @Test
    fun deleteRelationTest() = runBlocking {
        `when`(relationDao.deleteRelation(relationEntityId)).thenReturn(1)
        val actual = relationRepositoryImpl.deleteRelation(relationEntityId)
        Assert.assertEquals(1, actual)
    }

    @Test
    fun deleteRelationTest2() = runBlocking {
        `when`(relationDao.deleteRelation(id, relativeId)).thenReturn(1)
        val actual = relationRepositoryImpl.deleteRelation(id, relativeId)
        Assert.assertEquals(1, actual)
    }

    @Test
    fun deleteAllRelationOfPatientTest() = runBlocking {
        `when`(relationDao.deleteAllRelationOfPatient(id)).thenReturn(1)
        val actual = relationRepositoryImpl.deleteAllRelationOfPatient(id)
        Assert.assertEquals(1, actual)
    }
}