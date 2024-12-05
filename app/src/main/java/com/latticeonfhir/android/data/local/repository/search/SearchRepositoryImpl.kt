package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SymDiagSearchEntity
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchDiagnosisList
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchList
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchMedicationList
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchSymptomsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.LinkedList
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao,
    private val relationDao: RelationDao
) : SearchRepository {

    @Volatile
    private var searchPatientList: List<PatientAndIdentifierEntity>? = null

    override suspend fun getSearchList(): List<PatientAndIdentifierEntity> {
        return searchPatientList ?: searchDao.getPatientList().also { searchPatientList = it }
    }

    override fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val fuzzySearchList = getFuzzySearchList(
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
            pagingData.map { patientAndIdentifierEntity ->
                PaginationResponse(
                    patientAndIdentifierEntity.toPatientResponse(),
                    fuzzySearchList.size
                )
            }
        }
    }

    override fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>,
        existingMembers: Set<String>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val fuzzySearchList = getFuzzySearchList(
            searchList,
            searchParameters,
            68
        ).filter {
            !existingMembers.contains(it.patientEntity.id)
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
            pagingData.map { patientAndIdentifierEntity ->
                PaginationResponse(
                    patientAndIdentifierEntity.toPatientResponse(),
                    fuzzySearchList.size
                )
            }
        }
    }

    override fun searchPatientByQuery(
        query: String,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        return if (query.contains("[0-9]".toRegex())) {
            searchPatients(
                SearchParameters(
                    query,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                searchList
            )
        } else {
            searchPatients(
                SearchParameters(
                    null,
                    query,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                searchList
            )
        }
    }

    override suspend fun searchActiveIngredients(activeIngredient: String): List<String> {
        return getFuzzySearchMedicationList(activeIngredient, searchDao.getActiveIngredients(), 60)
    }

    override suspend fun insertRecentPatientSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.PATIENT).run {
            if (size == 5) {
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

    override suspend fun getSuggestedMembers(
        patientId: String,
        searchParameters: SearchParameters,
        returnList: (LinkedList<PatientResponse>) -> Unit
    ) {
        val linkedList = LinkedList<PatientResponse>()
        val existingMembers = relationDao.getAllRelationOfPatient(patientId)
            .map { relationEntity -> relationEntity.toId }.toMutableSet().apply { add(patientId) }
        getFuzzySearchList(
            getSearchList(),
            searchParameters,
            90
        ).filter {
            !existingMembers.contains(it.patientEntity.id)
        }.map { patientAndIdentifierEntity ->
            linkedList.add(patientAndIdentifierEntity.toPatientResponse())
        }
        returnList(
            linkedList
        )
    }

    override suspend fun getFiveSuggestedMembers(
        patientId: String,
        address: PatientAddressResponse
    ): List<PatientResponse> {
        var suggestionsList = listOf<PatientResponse>()
        getSuggestedMembers(
            patientId, SearchParameters(
                null,
                null,
                null,
                null,
                null,
                null,
                address.addressLine1,
                address.city,
                address.district,
                address.state,
                address.postalCode,
                address.addressLine2
            )
        ) { list ->
            suggestionsList = if (list.size > 5) {
                list.subList(0, 5)
            } else list
        }
        return suggestionsList
    }

    override suspend fun insertRecentSymptomAndDiagnosisSearch(
        searchQuery: String, searchTypeEnum: SearchTypeEnum, size: Int, date: Date
    ): Long {
        val symDiagSearchEntity = searchDao.getSearchByQuery(searchQuery)
        return insertSearch(
            symDiagSearchEntity = symDiagSearchEntity,
            searchTypeEnum = searchTypeEnum,
            date = date,
            searchQuery = searchQuery
        )

    }

    private suspend fun insertSearch(
        symDiagSearchEntity: SymDiagSearchEntity?,
        searchTypeEnum: SearchTypeEnum,
        date: Date,
        searchQuery: String
    ): Long {
        return if (symDiagSearchEntity == null) {
            searchDao.insertOrUpdateSearch(
                SymDiagSearchEntity(
                    searchQuery = searchQuery,
                    date = date,
                    searchCount = 1,
                    searchType = searchTypeEnum
                )
            )
        } else {
            searchDao.updateSearch(
                searchQuery = symDiagSearchEntity.searchQuery,
                searchCount = symDiagSearchEntity.searchCount + 1
            ).toLong()
        }

    }
    override suspend fun getRecentSymptomAndDiagnosisSearches(searchTypeEnum: SearchTypeEnum): List<String> {
        return searchDao.getMostFrequentSearches(searchTypeEnum)
    }

    override suspend fun searchSymptoms(searchQuery: String, gender: String?): List<String> {
        val symptoms = if (gender != GenderEnum.OTHER.value) {
            searchDao.getSymptoms().filter { it.gender == null || it.gender == gender }
                .map { it.display }
        } else {
            searchDao.getSymptoms().map { it.display }
        }
        return getFuzzySearchSymptomsList(searchQuery, symptoms, 60)

    }

    override suspend fun searchDiagnosis(searchQuery: String): List<String> {
        val diagnosis = searchDao.getDiagnosis().map { "${it.code}, ${it.display}" }
        return getFuzzySearchDiagnosisList(searchQuery, diagnosis, 60)

    }
}