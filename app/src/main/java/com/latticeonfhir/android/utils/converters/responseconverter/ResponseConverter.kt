package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PermanentAddressEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PersonAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity
import com.latticeonfhir.android.data.server.model.PersonAddressResponse
import com.latticeonfhir.android.data.server.model.PersonIdentifier
import com.latticeonfhir.android.data.server.model.PersonResponse
import java.util.Date
import java.util.UUID

fun PersonResponse.toGenericEntity(): GenericEntity {
    return GenericEntity(
        id = identifier?.get(0)?.identifierNumber ?: UUID.randomUUID().toString(),
        payload = FhirApp.gson.toJson(this),
        type = GenericTypeEnum.PERSON
    )
}

fun PersonResponse.toPersonEntity(): PatientEntity {
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

fun PersonAddressResponse.toPermanentAddressEntity(): PermanentAddressEntity {
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

fun PersonIdentifier.toIdentifierEntity(personId: String): IdentifierEntity {
    return IdentifierEntity(
        identifierNumber = identifierNumber,
        identifierType = identifierType,
        identifierCode = code,
        personId = personId
    )
}

fun PersonResponse.toListOfIdentifierEntity(): List<IdentifierEntity>? {
    return this.identifier?.map {
        it.toIdentifierEntity(this.id)
    }
}

fun PersonAndIdentifierEntity.toPersonResponse(): PersonResponse {
    return PersonResponse(
        id = patientEntity.id,
        firstName = patientEntity.firstName,
        middleName = patientEntity.middleName,
        lastName = patientEntity.lastName,
        identifier = identifiers.map { it.toPersonIdentifier() },
        active = patientEntity.active,
        gender = patientEntity.gender,
        birthDate = Date(patientEntity.birthDate),
        mobileNumber = patientEntity.mobileNumber,
        email = patientEntity.email,
        permanentAddress = patientEntity.permanentAddress.toPersonAddressResponse(),
        fhirId = patientEntity.fhirId
    )
}

fun IdentifierEntity.toPersonIdentifier(): PersonIdentifier {
    return PersonIdentifier(
        identifierType = identifierType,
        identifierNumber = identifierNumber,
        code = identifierCode
    )
}

fun PermanentAddressEntity.toPersonAddressResponse(): PersonAddressResponse {
    return PersonAddressResponse(
        addressLine1 = addressLine1,
        city = city,
        district = district,
        state = state,
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2
    )
}
