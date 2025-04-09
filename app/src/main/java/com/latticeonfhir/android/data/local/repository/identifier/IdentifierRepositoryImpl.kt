package com.latticeonfhir.android.data.local.repository.identifier

import com.latticeonfhir.core.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
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