package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface SearchRepository {

    /** Patient Search */
    suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PaginationResponse<PatientResponse>>>
    suspend fun filteredSearchPatients(patientId: String, searchParameters: SearchParameters): Flow<PagingData<PaginationResponse<PatientResponse>>>
    suspend fun searchPatientByQuery(query: String): Flow<PagingData<PaginationResponse<PatientResponse>>>

    /** Medication Search */
    suspend fun searchMedication(activeIngredient: String): List<MedicationResponse>

    /** Recent Patient Search*/
    suspend fun insertRecentPatientSearch(searchQuery: String): Long
    suspend fun getRecentPatientSearches(): List<String>

    /** Recent Medication Search*/
    suspend fun insertRecentMedicationSearch(searchQuery: String): Long
    suspend fun getRecentMedicationSearches(): List<String>

    suspend fun getSuggestedMembers(patientId: String, searchParameters: SearchParameters, returnList: (LinkedList<PatientResponse>) -> Unit)
}