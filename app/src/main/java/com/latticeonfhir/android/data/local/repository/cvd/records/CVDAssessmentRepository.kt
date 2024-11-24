package com.latticeonfhir.android.data.local.repository.cvd.records

import com.latticeonfhir.android.data.server.model.cvd.CVDResponse

interface CVDAssessmentRepository {
    suspend fun insertCVDRecord(vararg cvdResponse: CVDResponse): List<Long>
    suspend fun getCVDRecord(patientId: String): List<CVDResponse>
}