package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.search
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getPersonResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getRelatedPerson
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchMedicationList
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchPatientList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.ResourceType
import java.util.Date
import java.util.LinkedList
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val searchDao: SearchDao
) : SearchRepository {

    @Volatile
    private var searchPatientListFhir: List<Patient>? = null

    override suspend fun getSearchListFhir(): List<Patient> {
        if (searchPatientListFhir.isNullOrEmpty()){
            searchPatientListFhir = fhirEngine.search<Patient> {}.map {
                it.resource
            }
        }
        return searchPatientListFhir!!
    }

    override fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<Patient>
    ): Flow<PagingData<PaginationResponse<Patient>>> {
        val fuzzySearchList = getFuzzySearchPatientList(
            searchList,
            searchParameters,
            68
        )
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    fuzzySearchList,
                    PAGE_SIZE
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { patient ->
                PaginationResponse(
                    patient,
                    fuzzySearchList.size
                )
            }
        }
    }

    private suspend fun getExistingRelationIds(patientId: String): Set<String> {
        val existingMembers = mutableSetOf<String>()
        getPersonResource(fhirEngine, patientId)
            .link.forEach { relatedPersonLink ->
                if (relatedPersonLink.target.reference.contains(ResourceType.RelatedPerson.name)) {
                    getRelatedPerson(
                        fhirEngine,
                        relatedPersonLink.target.reference.substringAfter("/")
                    ).forEach { result ->
                        result.included?.get(RelatedPerson.PATIENT.paramName)?.get(0)?.logicalId?.let { id ->
                            existingMembers.add(
                                id
                            )
                        }
                    }
                }
            }
        return existingMembers
    }

    override suspend fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters,
        searchList: List<Patient>
    ): Flow<PagingData<PaginationResponse<Patient>>> {
        val existingMembers = getExistingRelationIds(patientId)
        val fuzzySearchList = getFuzzySearchPatientList(
            searchList,
            searchParameters,
            68
        ).filter {
            !existingMembers.contains(it.logicalId)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    fuzzySearchList,
                    PAGE_SIZE
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { patient ->
                PaginationResponse(
                    patient,
                    fuzzySearchList.size
                )
            }
        }
    }

    override suspend fun searchActiveIngredients(activeIngredient: String): List<String> {
        return getFuzzySearchMedicationList(activeIngredient, searchDao.getActiveIngredients(), 60)
    }

    override suspend fun insertRecentPatientSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.PATIENT).run {
            val duplicateId = searchDao.getIdOfDuplicateSearch(SearchTypeEnum.PATIENT, searchQuery)
            if (duplicateId != null){
                searchDao.deleteRecentSearch(duplicateId)
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.PATIENT
                    )
                )
            } else if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.PATIENT
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.PATIENT
                    )
                )
            }
        }
    }

    override suspend fun getRecentPatientSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.PATIENT)
    }

    override suspend fun insertRecentActiveIngredientSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT).run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.ACTIVE_INGREDIENT).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.ACTIVE_INGREDIENT
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.ACTIVE_INGREDIENT
                    )
                )
            }
        }
    }

    override suspend fun getRecentActiveIngredientSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)
    }

    private suspend fun getSuggestedMemberFhir(
        patientId: String,
        searchParameters: SearchParameters,
        returnList: (LinkedList<Patient>) -> Unit
    ) {
        val linkedList = LinkedList<Patient>()
        val existingMembers = getExistingRelationIds(patientId)
        getFuzzySearchPatientList(
            getSearchListFhir(),
            searchParameters,
            90
        ).filter {
            !existingMembers.contains(it.logicalId)
        }.map {
            linkedList.add(it)
        }
        returnList(
            linkedList
        )
    }


    override suspend fun getFiveSuggestedMembersFhir(
        patientId: String,
        address: Address
    ): List<Patient> {
        var suggestionsList = listOf<Patient>()
        val addressLine2 = if (address.line.size >1) address.line[1].value else ""
        getSuggestedMemberFhir(
            patientId, SearchParameters(
                null,
                null,
                null,
                null,
                null,
                null,
                address.line[0].value,
                address.city,
                address.district?:"",
                address.state,
                address.postalCode,
                addressLine2
            )
        ) { list ->
            suggestionsList = if (list.size > 5) {
                list.subList(0, 5)
            } else list
        }
        return suggestionsList
    }
}