package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues.COUNT_VALUE
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.EndPoints.RELATED_PERSON
import com.latticeonfhir.android.data.server.constants.QueryParameters.COUNT
import com.latticeonfhir.android.data.server.constants.QueryParameters.ID
import com.latticeonfhir.android.data.server.constants.QueryParameters.OFFSET
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiContinueResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val patientDao: PatientDao,
    private val genericDao: GenericDao
) : SyncRepository {

    override suspend fun getAndInsertListPatientData(offset: Int): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(
            apiService.getListData(
                PATIENT,
                mapOf(Pair(COUNT, COUNT_VALUE.toString()), Pair(OFFSET, offset.toString()))
            ),
            true
        ).apply {
            if (this is ApiContinueResponse) {
                patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())
                body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    getAndInsertListPatientData(offset + 20)
                }
            }
            if (this is ApiEndResponse) {
                patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())
                body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
            }
        }
    }

    override suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(
            apiService.getListData(
                PATIENT,
                mapOf(Pair(ID, id))
            )
        ).apply {
            if (this is ApiContinueResponse) {
                patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())
                body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
            }
            if (this is ApiEndResponse) {
                patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())
                body.map { patientResponse ->
                    patientResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                        patientDao.insertIdentifiers(*listOfIdentifiers.filter { it.identifierCode != IdentifierCodeEnum.MEDICAL_RECORD.value }
                            .toTypedArray())
                    }
                }
            }
        }
    }

    override suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.POST
        ).run {
            if (this.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                apiService.createData(
                    PATIENT,
                    map { it.payload.fromJson<PatientResponse>() }
                )
            ).apply {
                if (this is ApiContinueResponse) {
                    CoroutineScope(Dispatchers.IO).launch {
                        genericDao.deleteSyncPayload(this@run.toListOfId()).also {
                            if (it > 0) sendPersonPostData()
                        }
                    }
                    body
                }
                if (this is ApiErrorResponse) {
                    errorMessage
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.RELATION,
            syncType = SyncType.POST
        ).run {
            if (this.isEmpty()) ApiEmptyResponse()
            else ApiResponseConverter.convert(
                apiService.createData(
                    PATIENT,
                    map { it.payload.fromJson<RelatedPersonResponse>() }
                )
            ).apply {
                if (this is ApiContinueResponse) {
                    CoroutineScope(Dispatchers.IO).launch {
                        genericDao.deleteSyncPayload(this@run.toListOfId()).also {
                            if (it > 0) sendRelatedPersonPostData()
                        }
                    }
                    body
                }
                if (this is ApiErrorResponse) {
                    errorMessage
                }
            }
        }
    }

    override suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.PATCH
        ).run {
            if (isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    apiService.patchListOfChanges(
                        PATIENT,
                        map { it.payload.fromJson() }
                    )
                ).apply {
                    if (this is ApiContinueResponse) {
                        CoroutineScope(Dispatchers.IO).launch {
                            genericDao.deleteSyncPayload(this@run.toListOfId()).also {
                                if (it > 0) sendPersonPatchData()
                            }
                        }
                        body
                    }
                    if (this is ApiErrorResponse) {
                        errorMessage
                    }
                }
            }
        }
    }

    override suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return genericDao.getSameTypeGenericEntityPayload(GenericTypeEnum.RELATION, SyncType.PATCH)
            .run {
                if (isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        apiService.patchListOfChanges(
                            RELATED_PERSON,
                            map { it.payload.fromJson() }
                        )
                    ).apply {
                        if (this is ApiContinueResponse) {
                            CoroutineScope(Dispatchers.IO).launch {
                                genericDao.deleteSyncPayload(this@run.toListOfId()).also {
                                    if (it > 0) sendRelatedPersonPatchData()
                                }
                            }
                            body
                        }
                        if (this is ApiErrorResponse) {
                            errorMessage
                        }
                    }
                }
            }
    }
}