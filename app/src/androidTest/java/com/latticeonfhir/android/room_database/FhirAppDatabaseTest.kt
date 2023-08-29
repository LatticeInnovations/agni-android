package com.latticeonfhir.android.room_database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
open class FhirAppDatabaseTest: TestCase() {

    lateinit var fhirAppDatabase: FhirAppDatabase
    lateinit var patientDao: PatientDao
    lateinit var genericDao: GenericDao
    lateinit var identifierDao: IdentifierDao
    lateinit var relationDao: RelationDao
    lateinit var searchDao: SearchDao
    lateinit var medicationDao: MedicationDao
    lateinit var prescriptionDao: PrescriptionDao
    lateinit var scheduleDao: ScheduleDao
    lateinit var appointmentDao: AppointmentDao

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
        patientFhirId = patientResponse.fhirId,
        relation = RelationEnum.SPOUSE,
        relativeFirstName = relative.firstName,
        relativeId = relativeId,
        relativeLastName = relative.lastName,
        relativeMiddleName = relative.middleName,
        relativeGender = relative.gender,
        relativeFhirId = relative.fhirId
    )

    val date = Date()

    val scheduleResponse = ScheduleResponse(
        uuid = id,
        scheduleId = "SCHEDULE_FHIR_ID",
        orgId = "ORG_FHIR_ID",
        bookedSlots = 1,
        planningHorizon = Slot(
            start = date,
            end = date
        )
    )

    val appointmentResponse = AppointmentResponse(
        uuid = id,
        appointmentId = "APPOINTMENT_FHIR_ID",
        createdOn = date,
        orgId = "ORG_ID",
        patientFhirId = id,
        scheduleId = id,
        status = AppointmentStatusEnum.SCHEDULED.value,
        slot = Slot(
            start = date,
            end = date
        )
    )

    val appointmentResponseLocal = AppointmentResponseLocal(
        uuid = id,
        appointmentId = "APPOINTMENT_FHIR_ID",
        createdOn = date,
        orgId = "ORG_ID",
        patientId = id,
        scheduleId = date,
        status = AppointmentStatusEnum.SCHEDULED.value,
        slot = Slot(
            start = date,
            end = date
        )
    )

    protected val searchParameters = SearchParameters(
        null,
        "Test",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )


    @Before
    public override fun setUp() {
        super.setUp()
        fhirAppDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FhirAppDatabase::class.java
        ).allowMainThreadQueries().build()

        patientDao = fhirAppDatabase.getPatientDao()
        genericDao = fhirAppDatabase.getGenericDao()
        identifierDao = fhirAppDatabase.getIdentifierDao()
        searchDao = fhirAppDatabase.getSearchDao()
        relationDao = fhirAppDatabase.getRelationDao()
        medicationDao = fhirAppDatabase.getMedicationDao()
        prescriptionDao = fhirAppDatabase.getPrescriptionDao()
        scheduleDao = fhirAppDatabase.getScheduleDao()
        appointmentDao = fhirAppDatabase.getAppointmentDao()
    }

    @Test
    fun databaseBuilderTest(){
        assertEquals(true, this::fhirAppDatabase.isInitialized)
    }

    @After
    public override fun tearDown() {
        fhirAppDatabase.close()
    }
}