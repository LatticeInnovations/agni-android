package com.latticeonfhir.android.base

import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.data.server.model.user.UserRoleDetails
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import junit.framework.TestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Date

@RunWith(JUnit4::class)
abstract class BaseClass : TestCase() {
    val id = UUIDBuilder.generateUUID()
    val patientIdentifier = PatientIdentifier(
        identifierNumber = "PATIENT123",
        identifierType = "https//patient.id//.com",
        code = null
    )
    val relativeId = UUIDBuilder.generateUUID()
    val relationEntityId = UUIDBuilder.generateUUID()
    val relationSpouse = RelationEnum.SPOUSE

    val relationEntity = RelationEntity(
        id = relationEntityId,
        fromId = id,
        toId = relativeId,
        relation = relationSpouse
    )
    private val relationIdBrother = UUIDBuilder.generateUUID()
    private val relationIdInverseBrother = UUIDBuilder.generateUUID()
    val relationEntityBrother = RelationEntity(
        id = relationIdBrother,
        fromId = id,
        toId = relativeId,
        relation = RelationEnum.BROTHER
    )
    val relationEntityInverseBrother = RelationEntity(
        id = relationIdInverseBrother,
        fromId = relativeId,
        toId = id,
        relation = RelationEnum.BROTHER
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
        fhirId = "2132",
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

    val relationResponse = RelatedPersonResponse(
        id = "FHIR_ID",
        relationship = listOf(
            Relationship(
                patientIs = "BRO",
                relativeId = "FHIR_ID"
            )
        )
    )

    val newRelationResponse = RelatedPersonResponse(
        id = "FHIR_ID",
        relationship = listOf(
            Relationship(
                patientIs = "MTH",
                relativeId = "FHIR_ID"
            )
        )
    )

    val updatedRelationResponse = RelatedPersonResponse(
        id = "FHIR_ID",
        relationship = listOf(
            Relationship(
                patientIs = "BRO",
                relativeId = "FHIR_ID"
            ),
            Relationship(
                patientIs = "MTH",
                relativeId = "FHIR_ID"
            )
        )
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
        relativeFhirId = relative.fhirId,
        relativeGender = relative.gender
    )

    val relation = Relation(
        patientId = patientResponse.id,
        relativeId = relative.id,
        relation = RelationEnum.SPOUSE.value
    )
    val relationBrother = Relation(
        patientId = patientResponse.id,
        relativeId = relative.id,
        relation = RelationEnum.BROTHER.value
    )

    val createResponse = CreateResponse(
        status = "200 OK",
        fhirId = "21292",
        id = "78e2d936-39e4-42c3-abf4-b96274726c27",
        error = null
    )

    val medicationResponse = MedicationResponse(
        medFhirId = "21111",
        medCode = "323584006",
        medName = "Ampicillin sodium 500 milligram/1 vial Powder for solution for injection",
        doseForm = "Powder for solution for injection",
        doseFormCode = "385223009",
        activeIngredient = "ampicillin",
        activeIngredientCode = "387170002",
        medUnit = "vial",
        medNumeratorVal = 1.0
    )

    val date = Date()

    val prescribedResponse = PrescriptionResponse(
        prescriptionId = "78e2d936-39e4-42c3-abf4-b96274726c27",
        prescriptionFhirId = "21292",
        generatedOn = date,
        patientFhirId = "FHIR_ID",
        appointmentId = "APPOINTMENT_ID",
        prescription = listOf(
            Medication(
                medFhirId = "21117",
                note = "As prescribed by doctor",
                qtyPerDose = 1,
                frequency = 1,
                doseForm = "Tablet",
                duration = 7,
                timing = "1521000175104",
                qtyPrescribed = 7
            )
        )
    )

    val medicineTimeResponse = MedicineTimeResponse(
        medInstructionVal = "Before meal",
        medInstructionCode = "307165006"
    )

    protected val user = UserResponse(
        userId = "USER_FHIR_ID",
        userName = "USER_NAME",
        userEmail = "USER_EMAIL",
        mobileNumber = 9999999999,
        role = listOf(
            UserRoleDetails(
                roleId = "ROLE_ID",
                role = "ROLE",
                orgId = "ORG_ID",
                orgName = "ORG_NAME"
            )
        )
    )

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
         patientFhirId = "PATIENT_FHIR_ID",
         scheduleId = "SCHEDULE_FHIR_ID",
         status = AppointmentStatusEnum.SCHEDULED.value,
         slot = Slot(
             start = date,
             end = date
         )
     )

    val completedAppointmentResponse = AppointmentResponse(
        uuid = id,
        appointmentId = "APPOINTMENT_FHIR_ID",
        createdOn = date,
        orgId = "ORG_ID",
        patientFhirId = "PATIENT_FHIR_ID",
        scheduleId = "SCHEDULE_FHIR_ID",
        status = AppointmentStatusEnum.COMPLETED.value,
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
        patientId = "PATIENT_FHIR_ID",
        scheduleId = date,
        status = AppointmentStatusEnum.SCHEDULED.value,
        slot = Slot(
            start = date,
            end = date
        )
    )

    val appointmentResponseLocalNullFhirId = AppointmentResponseLocal(
        uuid = id,
        appointmentId = null,
        createdOn = date,
        orgId = "ORG_ID",
        patientId = "PATIENT_FHIR_ID",
        scheduleId = date,
        status = AppointmentStatusEnum.SCHEDULED.value,
        slot = Slot(
            start = date,
            end = date
        )
    )

    val completedAppointmentResponseLocal = AppointmentResponseLocal(
        uuid = id,
        appointmentId = "APPOINTMENT_FHIR_ID",
        createdOn = date,
        orgId = "ORG_ID",
        patientId = "PATIENT_FHIR_ID",
        scheduleId = date,
        status = AppointmentStatusEnum.COMPLETED.value,
        slot = Slot(
            start = date,
            end = date
        )
    )
}