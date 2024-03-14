package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Patient
import java.util.Date

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

    // TODO: to be removed after Add Household member screen binding
    /** Get Suggested Members */
    suspend fun getFiveSuggestedMembers(
        patientId: String,
        address: PatientAddressResponse
    ): List<PatientResponse>

    // TODO: to be removed after whole binding
    suspend fun getSearchList(): List<PatientAndIdentifierEntity>

    suspend fun getFiveSuggestedMembersFhir(
        patientId: String,
        address: Address
    ): List<Patient>

    suspend fun getSearchListFhir(): List<Patient>
}