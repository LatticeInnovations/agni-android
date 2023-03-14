package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.constants.EndPoints.Patient
import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.data.server.constants.QueryParameters.ID
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val patientDao: PatientDao
) : SyncRepository {

    override suspend fun getListPatientData(): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(apiService.getListPatientData(Patient, emptyMap()))
            .apply {
                if (this is ApiSuccessResponse) {
                    patientDao.insertListPatientData(this.body.map { it.toPatientEntity() })
                    this.body.map { patientResponse ->
                        patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                            patientDao.insertListOfIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                                .toTypedArray())
                        }
                    }
                }
                if (this is ApiEndResponse) {
                    patientDao.insertListPatientData(this.body.map { it.toPatientEntity() })
                    this.body.map { patientResponse ->
                        patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                            patientDao.insertListOfIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                                .toTypedArray())
                        }
                    }
                }
            }
    }

    override suspend fun getPatientDataById(id: String): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(
            apiService.getListPatientData(
                Patient,
                mapOf(Pair(ID, id))
            )
        ).apply {
            if (this is ApiSuccessResponse) {
                patientDao.insertListPatientData(this.body.map { it.toPatientEntity() })
                this.body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertListOfIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
            }
            if (this is ApiEndResponse) {
                patientDao.insertListPatientData(this.body.map { it.toPatientEntity() })
                this.body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertListOfIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
            }
        }
    }
}