package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class IdentifierDaoTest : FhirAppDatabaseTest() {

    @Test
    fun insertListOfIdentifierTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = identifierDao.insertListOfIdentifier(
            patientResponse.toListOfIdentifierEntity()
        )
        Assert.assertNotEquals("Identifier entity list not inserted.", -1, result)
    }

    @Test
    fun insertIdentifierTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = identifierDao.insertIdentifier(
            patientIdentifier.toIdentifierEntity(id)
        )
        Assert.assertNotEquals("Identifier entity not inserted.", -1, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletedIdentifiersTest() = runTest {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val deletedIdentifier = IdentifierEntity(
            identifierType = "https//nsvp.com",
            identifierNumber = "XYZ1234567",
            identifierCode = null,
            patientId = id
        )
        val result = identifierDao.deleteIdentifier(deletedIdentifier)
        Assert.assertNotEquals("Identifier entity not deleted.", -1, result)
    }
}