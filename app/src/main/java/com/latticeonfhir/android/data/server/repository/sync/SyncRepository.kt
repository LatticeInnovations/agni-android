package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SyncRepository {

    //Patient
    suspend fun getAndInsertListPatientData(): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>>
    suspend fun sendPersonPostData():  ResponseMapper<List<CreateResponse>>
    suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>>

    //Related-Person
    suspend fun sendRelatedPersonData(fhirId: String): ResponseMapper<List<CreateResponse>>
}