package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface SearchRepository {

    suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PaginationResponse<PatientResponse>>>
    suspend fun filteredSearchPatients(patientId: String, searchParameters: SearchParameters): Flow<PagingData<PaginationResponse<PatientResponse>>>
    suspend fun searchPatientByQuery(query: String): Flow<PagingData<PaginationResponse<PatientResponse>>>

    suspend fun insertRecentSearch(searchQuery: String, searchTypeEnum: SearchTypeEnum): Long
    suspend fun getRecentSearches(searchTypeEnum: SearchTypeEnum): List<String>

    suspend fun getSuggestedMembers(patientId: String, searchParameters: SearchParameters, returnList: (LinkedList<PatientResponse>) -> Unit)
}