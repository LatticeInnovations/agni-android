package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Patient
import java.util.Date

interface SearchRepository {

    /** Patient Search */
    fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<Patient>
    ): Flow<PagingData<PaginationResponse<Patient>>>

    suspend fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters,
        searchList: List<Patient>
    ): Flow<PagingData<PaginationResponse<Patient>>>

    /** Medication Search */
    suspend fun searchActiveIngredients(activeIngredient: String): List<String>

    /** Recent Patient Search*/
    suspend fun insertRecentPatientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentPatientSearches(): List<String>

    /** Recent Medication Search*/
    suspend fun insertRecentActiveIngredientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentActiveIngredientSearches(): List<String>

    /** Suggested member search*/
    suspend fun getFiveSuggestedMembersFhir(
        patientId: String,
        address: Address
    ): List<Patient>
    suspend fun getSearchListFhir(): List<Patient>
}