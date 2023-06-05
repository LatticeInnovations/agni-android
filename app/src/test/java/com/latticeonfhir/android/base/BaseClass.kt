package com.latticeonfhir.android.base

import com.latticeonfhir.android.data.local.enums.RelationEnum
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

    val createResponse = CreateResponse(
        status = "200 OK",
        fhirId = "21292",
        id = "urn:uuid:78e2d936-39e4-42c3-abf4-b96274726c27",
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

    val prescribedResponse = PrescriptionResponse(
        prescriptionId = "78e2d936-39e4-42c3-abf4-b96274726c27",
        prescriptionFhirId = "21292",
        generatedOn = Date(),
        patientFhirId = "20154",
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
}