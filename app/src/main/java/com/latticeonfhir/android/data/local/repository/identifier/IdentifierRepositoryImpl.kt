package com.latticeonfhir.android.data.local.repository.identifier

import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import javax.inject.Inject

class IdentifierRepositoryImpl @Inject constructor(private val identifierDao: IdentifierDao) :
    IdentifierRepository {

    override suspend fun insertIdentifierList(patientResponse: PatientResponse)  {
        identifierDao.insertListOfIdentifier(patientResponse.toListOfIdentifierEntity()!!)
    }

}