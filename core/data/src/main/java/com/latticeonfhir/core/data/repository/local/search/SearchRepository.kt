package com.latticeonfhir.core.data.repository.local.search

import androidx.paging.PagingData
import com.latticeonfhir.core.data.local.enums.SearchTypeEnum
import com.latticeonfhir.core.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.core.data.local.model.search.SearchParameters
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.core.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.LinkedList

interface SearchRepository {

    /** Patient Search */
    fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters, searchList: List<PatientAndIdentifierEntity>,
        existingMembers: Set<String>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    fun searchPatientByQuery(
        query: String,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    /** Medication Search */
    suspend fun searchActiveIngredients(activeIngredient: String): List<String>

    /** Recent Patient Search*/
    suspend fun insertRecentPatientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentPatientSearches(): List<String>

    /** Recent Medication Search*/
    suspend fun insertRecentActiveIngredientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentActiveIngredientSearches(): List<String>

    /** Get Suggested Members */
    suspend fun getSuggestedMembers(
        patientId: String,
        searchParameters: SearchParameters,
        returnList: (LinkedList<PatientResponse>) -> Unit
    )

    suspend fun getFiveSuggestedMembers(
        patientId: String,
        address: PatientAddressResponse
    ): List<PatientResponse>

    suspend fun getSearchList(): List<PatientAndIdentifierEntity>

    /** Recent Symptoms Search*/
    suspend fun insertRecentSymptomAndDiagnosisSearch(searchQuery: String, searchTypeEnum: SearchTypeEnum, size:Int, date: Date = Date()): Long
    suspend fun getRecentSymptomAndDiagnosisSearches(searchTypeEnum: SearchTypeEnum): List<String>

    suspend fun searchSymptoms(searchQuery: String, gender:String?): List<String>
    suspend fun searchDiagnosis(searchQuery: String): List<String>
}