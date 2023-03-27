package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class IdentifierDaoTest: BaseClass() {

    @Test
    fun insertListOfIdentifierTest() = runBlocking{
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = identifierDao.insertListOfIdentifier(
            patientResponse.toListOfIdentifierEntity()!!
        )
        Assert.assertNotEquals("Identifier entity list not inserted.", -1, result)
    }

    @Test
    fun insertIdentifierTest() = runBlocking{
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = identifierDao.insertIdentifier(
            patientIdentifier.toIdentifierEntity(id)
        )
        Assert.assertNotEquals("Identifier entity not inserted.", -1, result)
    }
}