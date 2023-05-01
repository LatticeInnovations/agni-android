package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SyncRepository {

    suspend fun getAndInsertListPatientData(offset: Int): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertRelation(): ResponseMapper<List<RelatedPersonResponse>>

    //POST
    suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRelatedPersonPostData(): ResponseMapper<List<CreateResponse>>

    //PATCH
    suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRelatedPersonPatchData(): ResponseMapper<List<CreateResponse>>
}