package com.latticeonfhir.core.data.repository.local.symptomsanddiagnosis

import com.latticeonfhir.core.database.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.core.model.local.symdiag.SymptomsAndDiagnosisLocal
import com.latticeonfhir.core.network.utils.responseconverter.toSymptomsAndDiagnosisEntity
import com.latticeonfhir.core.network.utils.responseconverter.toSymptomsAndDiagnosisLocal
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