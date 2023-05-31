package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RelationDaoTest: FhirAppDatabaseTest() {

    @Test
    fun insertRelationTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        patientDao.insertPatientData(relative.toPatientEntity())
        val result = relationDao.insertRelation(relationEntity)
        Assert.assertNotEquals("Relation not inserted.",-1, result)
        Assert.assertEquals("On successful insertion, should return List<Long> of size 1.",1, result.size)
    }

    @Test
    fun getRelationTest() = runBlocking {
        insertRelationTest()
        val result = relationDao.getRelation(id, relativeId)
        Assert.assertEquals("Returned relation view is not correct", listOf(relationView), result)
    }

    @Test
    fun getAllRelationOfPatientTest() = runBlocking {
        insertRelationTest()
        val result = relationDao.getAllRelationOfPatient(id)
        Assert.assertEquals("Returned relation entity is not correct", listOf(relationEntity), result)
    }

    @Test
    fun deleteRelationTest() = runBlocking {
        insertRelationTest()
        val result = relationDao.deleteRelation(id, relativeId)
        Assert.assertEquals("Relation not deleted", 1, result)
    }

    @Test
    fun deleteAllRelationOfPatientTest() = runBlocking {
        insertRelationTest()
        val result = relationDao.deleteAllRelationOfPatient(id)
        Assert.assertEquals("Relation not deleted", 1, result)
    }

    @Test
    fun updateRelationTest() = runBlocking {
        insertRelationTest()
        val result = relationDao.updateRelation(RelationEnum.NIECE, id, relativeId)
        Assert.assertEquals("Relation not updated", 1, result)
    }
}