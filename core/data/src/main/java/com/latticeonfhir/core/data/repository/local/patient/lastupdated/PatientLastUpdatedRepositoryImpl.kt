package com.latticeonfhir.core.data.repository.local.patient.lastupdated

import com.latticeonfhir.core.database.dao.PatientLastUpdatedDao
import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse
import javax.inject.Inject

class PatientLastUpdatedRepositoryImpl @Inject constructor(
    private val patientLastUpdatedDao: PatientLastUpdatedDao
) : PatientLastUpdatedRepository {
    override suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long {
        return patientLastUpdatedDao.insertPatientLastUpdatedData(patientLastUpdatedResponse.toPatientLastUpdatedEntity())[0]
    }

}