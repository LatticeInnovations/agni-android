package com.latticeonfhir.core.data.repository.local.identifier

import com.latticeonfhir.core.database.dao.IdentifierDao
import com.latticeonfhir.core.model.server.patient.PatientIdentifier
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toListOfIdentifierEntity
import javax.inject.Inject

class IdentifierRepositoryImpl @Inject constructor(private val identifierDao: IdentifierDao) :
    IdentifierRepository {

    override suspend fun insertIdentifierList(patientResponse: PatientResponse) {
        identifierDao.insertListOfIdentifier(patientResponse.toListOfIdentifierEntity())
    }

    override suspend fun deleteIdentifier(
        vararg patientIdentifier: PatientIdentifier,
        patientId: String
    ) {
        identifierDao.deleteIdentifier(*patientIdentifier.map { identifier ->
            identifier.toIdentifierEntity(patientId)
        }.toTypedArray())
    }

}