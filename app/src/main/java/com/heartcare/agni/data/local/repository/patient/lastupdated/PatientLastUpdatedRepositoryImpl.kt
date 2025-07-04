package com.heartcare.agni.data.local.repository.patient.lastupdated

import com.heartcare.agni.data.local.roomdb.dao.PatientLastUpdatedDao
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.utils.converters.responseconverter.toPatientLastUpdatedEntity
import javax.inject.Inject

class PatientLastUpdatedRepositoryImpl @Inject constructor(
    private val patientLastUpdatedDao: PatientLastUpdatedDao
) : PatientLastUpdatedRepository {
    override suspend fun insertPatientLastUpdatedData(patientLastUpdatedResponse: PatientLastUpdatedResponse): Long {
        return patientLastUpdatedDao.insertPatientLastUpdatedData(patientLastUpdatedResponse.toPatientLastUpdatedEntity())[0]
    }

}