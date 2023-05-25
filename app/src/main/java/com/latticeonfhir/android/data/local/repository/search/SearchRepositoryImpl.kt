package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.latticeonfhir.android.data.local.model.PaginationResponse
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.SearchHistoryEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchList
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
    private var searchList: List<PatientAndIdentifierEntity>? = null

    private suspend fun getSearchList(): List<PatientAndIdentifierEntity> {
        return searchList ?: searchDao.getPatientList().also { searchList = it }
    }

    override suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val searchList = getSearchList()
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

    override suspend fun filteredSearchPatients(
        patientId: String, searchParameters: SearchParameters
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val searchList = getSearchList()
        val existingMembers =
            relationDao.getAllRelationOfPatient(patientId).map { it.toId }.toMutableSet()
                .apply { add(patientId) }
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

    override suspend fun searchPatientByQuery(query: String): Flow<PagingData<PaginationResponse<PatientResponse>>> {
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
                )
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
                )
            )
        }
    }

    override suspend fun insertRecentSearch(searchQuery: String): Long {
        return searchDao.getRecentSearches().run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId().run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = Date()
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = Date()
                    )
                )
            }
        }
    }

    override suspend fun getRecentSearches(): List<String> {
        return searchDao.getRecentSearches()
    }

    override suspend fun getSuggestedMembers(
        patientId: String,
        searchParameters: SearchParameters,
        returnList: (LinkedList<PatientResponse>) -> Unit
    ) {
        val linkedList = LinkedList<PatientResponse>()
        val existingMembers =
            relationDao.getAllRelationOfPatient(patientId).map { it.toId }.toMutableSet()
                .apply { add(patientId) }
        getFuzzySearchList(
            getSearchList(),
            searchParameters,
            90
        ).filter {
            !existingMembers.contains(it.patientEntity.id)
        }.map {
            linkedList.add(it.toPatientResponse())
        }
        returnList(
            linkedList
        )
    }
}