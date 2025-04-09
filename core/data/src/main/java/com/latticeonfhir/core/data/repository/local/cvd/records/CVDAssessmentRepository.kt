package com.latticeonfhir.core.data.repository.local.cvd.records

import com.latticeonfhir.android.data.server.model.cvd.CVDResponse

interface CVDAssessmentRepository {
    suspend fun insertCVDRecord(vararg cvdResponse: CVDResponse): List<Long>
    suspend fun getCVDRecord(patientId: String): List<CVDResponse>
    suspend fun getTodayCVDRecord(patientId: String, startTime: Long, endTime: Long): CVDResponse?
    suspend fun updateCVDRecord(cvdResponse: CVDResponse): Int
}