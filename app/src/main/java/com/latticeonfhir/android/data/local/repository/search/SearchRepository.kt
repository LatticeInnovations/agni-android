package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList
import java.util.concurrent.SynchronousQueue

interface SearchRepository {

    suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PatientResponse>>
    suspend fun searchPatientByQuery(query: String): Flow<PagingData<PatientResponse>>

    suspend fun insertRecentSearch(searchQuery: String): Long
    suspend fun getRecentSearches(): List<String>

    suspend fun getSuggestedMembers(patientId: String, searchParameters: SearchParameters, returnList: (LinkedList<PatientResponse>) -> Unit)
}