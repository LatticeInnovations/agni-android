package com.latticeonfhir.android.room_database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import org.junit.After
import org.junit.Before
import java.util.*

open class BaseClass {

    lateinit var fhirAppDatabase: FhirAppDatabase
    lateinit var patientDao: PatientDao
    lateinit var genericDao: GenericDao
    lateinit var identifierDao: IdentifierDao
    lateinit var relationDao: RelationDao
    lateinit var searchDao: SearchDao

    val id = UUIDBuilder.generateUUID()
    val relativeId = UUIDBuilder.generateUUID()
    val relationEntityId = UUIDBuilder.generateUUID()

    val relationEntity = RelationEntity(
        id = relationEntityId,
        fromId = id,
        toId = relativeId,
        relation = RelationEnum.SPOUSE
    )

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

    val relative = PatientResponse(
        id = relativeId,
        firstName = "Relative",
        middleName = null,
        lastName = null,
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

    val relationView = RelationView(
        id = relationEntityId,
        patientFirstName = patientResponse.firstName,
        patientGender = patientResponse.gender,
        patientId = id,
        patientLastName = patientResponse.lastName,
        patientMiddleName = patientResponse.middleName,
        relation = RelationEnum.SPOUSE,
        relativeFirstName = relative.firstName,
        relativeId = relativeId,
        relativeLastName = relative.lastName,
        relativeMiddleName = relative.middleName,
        relativeGender = relative.gender
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
        searchDao = fhirAppDatabase.getSearchDao()
        relationDao = fhirAppDatabase.getRelationDao()
    }


    @After
    fun tearDown() {
        fhirAppDatabase.close()
    }
}