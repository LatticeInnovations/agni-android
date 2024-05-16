package com.latticeonfhir.android.data.local.repository.patient.lastupdated

import com.latticeonfhir.android.data.local.roomdb.dao.PatientLastUpdatedDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import javax.inject.Inject

class PatientLastUpdatedRepositoryImpl @Inject constructor(
    private val patientLastUpdatedDao: PatientLastUpdatedDao
): PatientLastUpdatedRepository {
    override suspend fun insertPatientLastUpdatedData(patientLastUpdatedEntity: PatientLastUpdatedEntity): Long {
        return patientLastUpdatedDao.insertPatientLastUpdatedData(patientLastUpdatedEntity)
    }

}