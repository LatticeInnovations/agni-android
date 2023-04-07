package com.latticeonfhir.android.room_database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import org.junit.After
import org.junit.Before
import java.util.*

open class BaseClass {

    lateinit var fhirAppDatabase: FhirAppDatabase
    lateinit var patientDao: PatientDao
    lateinit var genericDao: GenericDao
    lateinit var identifierDao: IdentifierDao

    val id = UUIDBuilder.generateUUID()
    val patientIdentifier = PatientIdentifier(
            identifierNumber = "PATIENT123",
            identifierType = "https//patient.id//.com",
            code = null
        )
    val patientResponse = PatientResponse(
        id = id,
        firstName = "Test",
        middleName = null,
        lastName = null,
        birthDate = Date(469823400000),
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

    @Before
    fun setUp() {
        fhirAppDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FhirAppDatabase::class.java
        ).allowMainThreadQueries().build()
        patientDao = fhirAppDatabase.getPatientDao()
        genericDao = fhirAppDatabase.getGenericDao()
        identifierDao = fhirAppDatabase.getIdentifierDao()
    }


    @After
    fun tearDown() {
        fhirAppDatabase.close()
    }
}