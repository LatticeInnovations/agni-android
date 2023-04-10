package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PermanentAddressEntity
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.relation.Relation.getInverseRelation
import java.util.Date

fun PatientResponse.toPatientEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        middleName = middleName,
        lastName = lastName,
        active = active,
        gender = gender,
        birthDate = birthDate.toTimeInMilli(),
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

fun PatientIdentifier.toIdentifierEntity(patientId: String): IdentifierEntity {
    return IdentifierEntity(
        identifierNumber = identifierNumber,
        identifierType = identifierType,
        identifierCode = code,
        patientId = patientId
    )
}

fun PatientResponse.toListOfIdentifierEntity(): List<IdentifierEntity>? {
    return this.identifier.map {
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
        birthDate = Date(patientEntity.birthDate).time.toPatientDate(),
        mobileNumber = patientEntity.mobileNumber,
        email = patientEntity.email,
        permanentAddress = patientEntity.permanentAddress.toPatientAddressResponse(),
        fhirId = patientEntity.fhirId
    )
}

fun IdentifierEntity.toPatientIdentifier(): PatientIdentifier {
    return PatientIdentifier(
        identifierType = identifierType, identifierNumber = identifierNumber, code = identifierCode
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

fun RelationEntity.toReverseRelation(
    patientDao: PatientDao,
    inverseRelationEntity: (RelationEntity) -> Unit
) {
    getInverseRelation(this, patientDao) { relationEnum ->
        inverseRelationEntity(
            RelationEntity(
                id = UUIDBuilder.generateUUID(),
                toId = fromId,
                fromId = toId,
                relation = relationEnum
            )
        )
    }
}

fun List<GenericEntity>.toListOfId(): List<String> {
    return this.map { it.id }
}

fun Relationship.toRelationEntity(): RelationEntity {
    return RelationEntity(
        id = UUIDBuilder.generateUUID(),
        fromId = patientId,
        toId = relativeId,
        relation = RelationEnum.fromString(relation)
    )
}

fun RelationEntity.toRelationship(): Relationship {
    return Relationship(
        patientId = fromId, relativeId = toId, relation = relation.value
    )
}