package com.latticeonfhir.android.room_database

import androidx.paging.PagingSource
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert
import java.util.*

class PatientDaoTest: BaseClass() {

    @Test
    fun addPatientTest() = runBlocking{
        val result = patientDao.insertPatientData(patientResponse.toPatientEntity())
        Assert.assertNotEquals("Patient not inserted.",-1, result)
        Assert.assertEquals("On successful insertion, should return List<Long> of size 1.",1, result.size)
    }

    @Test
    fun getPatientDataByIdTest() = runBlocking{
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val patient = patientDao.getPatientDataById(id)
        Assert.assertEquals("The patient requested is not returned.", id, patient[0].toPatientResponse().id)
    }

    @Test
    fun getListPatientDataTest() = runBlocking{
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val patient = patientDao.getListPatientData()

        val actual = patient.load(PagingSource.LoadParams.Refresh(null, 10, false))

        Assert.assertEquals("Patient List returned is not correct.", (actual as? PagingSource.LoadResult.Page)?.data, listOf(
            PatientAndIdentifierEntity(patientResponse.toPatientEntity(), listOf())
        ))
    }

    @Test
    fun updatePatientDataTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val updatedPatientResponse = PatientResponse(
            id = id,
            firstName = "Test",
            middleName = null,
            lastName = "Updated",
            birthDate = Date(469823400000).time.toPatientDate(),
            email = "test@gmail.com",
            active = true,
            gender = "male",
            mobileNumber = 9876543210,
            fhirId = null,
            permanentAddress = PatientAddressResponse(
                postalCode = "111111",
                state = "Uttarakhand",
                addressLine1 = "H-123",
                addressLine2 = "Jagjeetpur",
                city = "Haridwar",
                country = "India",
                district = null
            ),
            identifier = listOf(patientIdentifier)
        )
        val result = patientDao.updatePatientData(updatedPatientResponse.toPatientEntity())
        Assert.assertEquals("On successful updation, number of rows affected should be 1.",1, result)
    }

    @Test
    fun updateIdentifiersTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val updatedIdentifierEntity = IdentifierEntity(
            identifierType = "https//nsvp.com",
            identifierNumber = "XYZ1234567",
            identifierCode = null,
            patientId = id
        )
        val result = patientDao.updateIdentifiers(updatedIdentifierEntity)
        Assert.assertNotEquals("Identifier entity not updated.",-1, result)
    }

    @Test
    fun updateFhirIdTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = patientDao.updateFhirId(patientResponse.id, "1234")
        Assert.assertEquals("On successful updation, number of rows affected should be 1.",1, result)
    }

    @Test
    fun getPatientIdByFhirIdTest() = runBlocking {
        updateFhirIdTest()
        val result = patientDao.getPatientIdByFhirId("1234")
        Assert.assertEquals("id returned does not match the id of patient", patientResponse.id, result)
    }
}