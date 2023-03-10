package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.enpoints.EndPoints
import com.latticeonfhir.android.data.server.enpoints.EndPoints.Patient
import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val patientDao: PatientDao,
    private val identifierDao: IdentifierDao
) : SyncRepository {

    override suspend fun getListPatientData(): ResponseMapper<List<PatientResponse>> {
        val response = ApiResponseConverter.convert(apiService.getListPatientData(Patient, emptyMap()))
        if (response is ApiSuccessResponse) {
            patientDao.insertListPatientData(response.body.map { PatientResponse ->
                PatientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                    identifierDao.insertListOfIdentifier(listOfIdentifiers)
                }
                PatientResponse.toPatientEntity()
            })
        }
        if (response is ApiEndResponse) {
            patientDao.insertListPatientData(response.body.map { PatientResponse ->
                PatientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                    identifierDao.insertListOfIdentifier(listOfIdentifiers)
                }
                PatientResponse.toPatientEntity()
            })
        }
        return response
    }

    override suspend fun getPatientDataById(id: String): ResponseMapper<List<PatientResponse>> {
        val response = ApiResponseConverter.convert(apiService.getListPatientData(Patient,mapOf(Pair("_id",id))))
        if (response is ApiSuccessResponse) {
            patientDao.insertListPatientData(response.body.map {
                it.toPatientEntity()
            })
        }
        if (response is ApiEndResponse) {
            patientDao.insertListPatientData(response.body.map {
                it.toPatientEntity()
            })
        }
        return response
    }
}