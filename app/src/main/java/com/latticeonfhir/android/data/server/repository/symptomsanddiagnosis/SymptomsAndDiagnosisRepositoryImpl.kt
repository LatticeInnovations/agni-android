package com.latticeonfhir.core.data.server.repository.symptomsanddiagnosis

import com.latticeonfhir.core.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.android.data.server.api.SymptomsAndDiagnosisService
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.Diagnosis
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.Symptoms
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.latticeonfhir.core.utils.converters.responseconverter.toDiagnosis
import com.latticeonfhir.core.utils.converters.responseconverter.toDiagnosisEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toSymptoms
import com.latticeonfhir.core.utils.converters.responseconverter.toSymptomsEntity
import com.latticeonfhir.core.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.core.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SymptomsAndDiagnosisRepositoryImpl @Inject constructor(
    private val apiService: SymptomsAndDiagnosisService, private val dao: SymptomsAndDiagnosisDao
) : SymptomsAndDiagnosisRepository {

    override suspend fun insertSymptoms(): ResponseMapper<List<Symptoms>> {
        return ApiResponseConverter.convert(
            apiService.getSymptoms(),
            true
        ).apply {
            if (this is ApiEndResponse) {
                this.body.apply {
                    if (this[0].symptoms.isNotEmpty()) {
                        this[0].symptoms.map {
                            dao.insertSymptomsEntity(it.toSymptomsEntity())
                        }
                    }

                }

            }


        }
    }

    override suspend fun insertDiagnosis(): ResponseMapper<List<Diagnosis>> {
        return ApiResponseConverter.convert(
            apiService.getDiagnosis(),
            true
        ).apply {
            if (this is ApiEndResponse) {
                this.body.apply {
                    if (this.isNotEmpty() && this[0].diagnosis.isNotEmpty()) {
                        this[0].diagnosis.map {
                            dao.insertDiagnosisEntity(it.toDiagnosisEntity())
                        }
                    }
                }
            }
        }
    }

    override suspend fun getSymptoms(): List<SymptomsItem> {
        return dao.getSymptomsEntity().map { it.toSymptoms() }
    }

    override suspend fun getDiagnosis(): List<SymptomsAndDiagnosisItem> {
        return dao.getDiagnosisEntity().map { it.toDiagnosis() }

    }
}