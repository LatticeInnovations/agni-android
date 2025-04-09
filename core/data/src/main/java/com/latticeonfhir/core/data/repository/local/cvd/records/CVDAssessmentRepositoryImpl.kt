package com.latticeonfhir.core.data.repository.local.cvd.records

import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.core.database.dao.CVDDao
import com.latticeonfhir.core.utils.converters.responseconverter.toCVDEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toCVDResponse
import javax.inject.Inject

class CVDAssessmentRepositoryImpl@Inject constructor(
    private val cvdDao: CVDDao
): CVDAssessmentRepository {
    override suspend fun insertCVDRecord(vararg cvdResponse: CVDResponse): List<Long> {
        return cvdDao.insertCVDRecord(
            *cvdResponse.map { it.toCVDEntity() }.toTypedArray()
        )
    }

    override suspend fun getCVDRecord(patientId: String): List<CVDResponse> {
        return cvdDao.getCVDRecords(patientId).map { it.toCVDResponse() }
    }

    override suspend fun getTodayCVDRecord(
        patientId: String,
        startTime: Long,
        endTime: Long
    ): CVDResponse? {
        return cvdDao.getTodayCVDRecords(patientId, startTime, endTime)?.toCVDResponse()
    }

    override suspend fun updateCVDRecord(cvdResponse: CVDResponse): Int {
        return cvdDao.updateCVDRecord(cvdResponse.toCVDEntity())
    }
}