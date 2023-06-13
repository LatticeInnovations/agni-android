package com.latticeonfhir.android.data.local.repository.identifier

import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import org.intellij.lang.annotations.Identifier
import timber.log.Timber
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
            identifier.toIdentifierEntity(patientId)}.toTypedArray())
    }

}