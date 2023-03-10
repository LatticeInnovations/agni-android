package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PermanentAddressEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity
import com.latticeonfhir.android.data.server.model.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.PatientIdentifier
import com.latticeonfhir.android.data.server.model.PatientResponse
import java.util.Date
import java.util.UUID

fun PatientResponse.toGenericEntity(): GenericEntity {
    return GenericEntity(
        id = identifier?.get(0)?.identifierNumber ?: UUID.randomUUID().toString(),
        payload = FhirApp.gson.toJson(this),
        type = GenericTypeEnum.Patient
    )
}

fun PatientResponse.toPatientEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        middleName = middleName,
        lastName = lastName,
        active = active,
        gender = gender,
        birthDate = birthDate.time,
        mobileNumber = mobileNumber,
        email = email,
        permanentAddress = permanentAddress.toPermanentAddressEntity(),
        fhirId = fhirId
    )
}

fun PatientAddressResponse.toPermanentAddressEntity(): PermanentAddressEntity {
    return PermanentAddressEntity(
        addressLine1 = addressLine1,
        city = city,
        district = district,
        state = state,
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2
    )
}

fun PatientIdentifier.toIdentifierEntity(PatientId: String): IdentifierEntity {
    return IdentifierEntity(
        identifierNumber = identifierNumber,
        identifierType = identifierType,
        identifierCode = code,
        PatientId = PatientId
    )
}

fun PatientResponse.toListOfIdentifierEntity(): List<IdentifierEntity>? {
    return this.identifier?.map {
        it.toIdentifierEntity(this.id)
    }
}

fun PatientAndIdentifierEntity.toPatientResponse(): PatientResponse {
    return PatientResponse(
        id = patientEntity.id,
        firstName = patientEntity.firstName,
        middleName = patientEntity.middleName,
        lastName = patientEntity.lastName,
        identifier = identifiers.map { it.toPatientIdentifier() },
        active = patientEntity.active,
        gender = patientEntity.gender,
        birthDate = Date(patientEntity.birthDate),
        mobileNumber = patientEntity.mobileNumber,
        email = patientEntity.email,
        permanentAddress = patientEntity.permanentAddress.toPatientAddressResponse(),
        fhirId = patientEntity.fhirId
    )
}

fun IdentifierEntity.toPatientIdentifier(): PatientIdentifier {
    return PatientIdentifier(
        identifierType = identifierType,
        identifierNumber = identifierNumber,
        code = identifierCode
    )
}

fun PermanentAddressEntity.toPatientAddressResponse(): PatientAddressResponse {
    return PatientAddressResponse(
        addressLine1 = addressLine1,
        city = city,
        district = district,
        state = state,
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2
    )
}
