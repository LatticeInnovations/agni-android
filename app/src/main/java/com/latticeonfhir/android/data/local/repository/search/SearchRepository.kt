package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PatientResponse>>
}