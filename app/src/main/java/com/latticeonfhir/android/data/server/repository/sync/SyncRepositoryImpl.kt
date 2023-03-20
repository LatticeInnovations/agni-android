package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.IdentifierCodeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.EndPoints.RELATED_PERSON
import com.latticeonfhir.android.data.server.constants.QueryParameters.ID
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonRequest
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val patientDao: PatientDao,
    private val genericDao: GenericDao
) : SyncRepository {

    override suspend fun getAndInsertListPatientData(): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(apiService.getListData(PATIENT, emptyMap()))
            .apply {
                if (this is ApiSuccessResponse) {
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

    override suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>> {
        return ApiResponseConverter.convert(
            apiService.getListData(
                PATIENT,
                mapOf(Pair(ID, id))
            )
        ).apply {
            if (this is ApiSuccessResponse) {
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
                if (this is ApiSuccessResponse) {
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
            if (this.isEmpty()) ApiEmptyResponse()
            else {
                ApiResponseConverter.convert(
                    apiService.patchListOfChanges(
                        PATIENT,
                        map { it.payload.fromJson<Map<String, Any>>() }
                    )
                ).apply {
                    if (this is ApiSuccessResponse) {
                        body
                    }
                    if (this is ApiErrorResponse) {
                        errorMessage
                    }
                }
            }
        }
    }

    override suspend fun sendRelatedPersonData(fhirId: String): ResponseMapper<List<CreateResponse>> {
        val list = mutableListOf<RelatedPersonRequest>()
        return genericDao.getSameTypeGenericEntityPayload(GenericTypeEnum.RELATION, SyncType.PATCH)
            .let { relatedRequestList ->
                if (relatedRequestList.isEmpty()) ApiEmptyResponse()
                else {
                    relatedRequestList.forEach { genericEntity ->
                        val map = mutableMapOf<String, Any>()
                        map["id"] = genericEntity.id
                        map.putAll(genericEntity.payload.fromJson<Map<String, List<ChangeRequest>>>())
                        list.add(
                            RelatedPersonRequest(
                                map
                            )
                        )
                    }
                    ApiResponseConverter.convert(
                        apiService.patchListOfChanges(
                            RELATED_PERSON,
                            list
                        )
                    ).run {
                        if (this is ApiSuccessResponse) {
                            ApiSuccessResponse(body)
                        } else {
                            ApiEmptyResponse()
                        }
                    }
                }
            }
    }
}