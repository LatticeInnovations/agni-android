package com.latticeonfhir.android.data.local.repository.patient

import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(private val patientDao: PatientDao): PatientRepository {

    override suspend fun getPatientList(): List<PatientResponse> {
        return patientDao.getListPatientData().map {
            it.toPatientResponse()
        }
    }

    override suspend fun updatePatientData(patientResponse: PatientResponse): Int {
        return patientDao.updatePatientData(patientResponse.toPatientEntity())
    }
}