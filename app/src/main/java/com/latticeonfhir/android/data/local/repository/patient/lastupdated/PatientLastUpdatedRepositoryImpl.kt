package com.latticeonfhir.core.data.local.repository.patient.lastupdated

import com.latticeonfhir.core.data.local.roomdb.dao.PatientLastUpdatedDao
import com.latticeonfhir.core.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.utils.converters.responseconverter.toPatientLastUpdatedEntity
import javax.inject.Inject

class PatientLastUpdatedRepositoryImpl @Inject constructor(
    private val patientLastUpdatedDao: PatientLastUpdatedDao
) : PatientLastUpdatedRepository {
    override suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long {
        return patientLastUpdatedDao.insertPatientLastUpdatedData(patientLastUpdatedResponse.toPatientLastUpdatedEntity())[0]
    }

}