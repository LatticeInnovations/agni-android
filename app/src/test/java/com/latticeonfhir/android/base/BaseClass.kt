package com.latticeonfhir.android.base

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import java.util.Date

open class BaseClass {
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
}