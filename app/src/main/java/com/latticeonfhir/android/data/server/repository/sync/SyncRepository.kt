package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.server.model.PatientResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SyncRepository {

    suspend fun getListPatientData(): ResponseMapper<List<PatientResponse>>
    suspend fun getPatientDataById(id: String): ResponseMapper<List<PatientResponse>>
}