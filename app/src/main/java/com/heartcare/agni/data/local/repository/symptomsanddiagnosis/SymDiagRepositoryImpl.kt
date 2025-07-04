package com.heartcare.agni.data.local.repository.symptomsanddiagnosis

import com.heartcare.agni.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.heartcare.agni.utils.converters.responseconverter.toSymptomsAndDiagnosisEntity
import com.heartcare.agni.utils.converters.responseconverter.toSymptomsAndDiagnosisLocal
import javax.inject.Inject

class SymDiagRepositoryImpl @Inject constructor(private val dao: SymptomsAndDiagnosisDao) :
    SymDiagRepository {
    override suspend fun insertSymptomsAndDiagnosis(local: SymptomsAndDiagnosisLocal): List<Long> {
        return dao.insertSymptomsAndDiagnosis(local.toSymptomsAndDiagnosisEntity())
    }

    override suspend fun getPastSymptomsAndDiagnosis(patientId: String): List<SymptomsAndDiagnosisLocal> {
        return dao.getPastSymptomsAndDiagnosis(patientId).map { it.toSymptomsAndDiagnosisLocal() }
    }

    override suspend fun updateSymDiagFhirId(symDiagUuid: String, fhirId: String) {
        dao.updateSymDiagFhirId(symDiagUuid, fhirId)
    }

    override suspend fun getSymDiagByAppointmentId(appointmentId: String): List<SymptomsAndDiagnosisLocal> {
        return dao.getSymDiagByAppointmentId(appointmentId).map { it.toSymptomsAndDiagnosisLocal() }
    }

    override suspend fun updateSymDiagData(symptomsAndDiagnosisLocal: SymptomsAndDiagnosisLocal): Int {
        return dao.updateSymDiagData(symptomAndDiagnosisEntity = symptomsAndDiagnosisLocal.toSymptomsAndDiagnosisEntity())
    }
}