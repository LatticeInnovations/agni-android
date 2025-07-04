package com.heartcare.agni.data.local.repository.identifier

import com.heartcare.agni.data.local.roomdb.dao.IdentifierDao
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.converters.responseconverter.toIdentifierEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfIdentifierEntity
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