package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.SearchHistoryEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchDao: SearchDao) : SearchRepository {

    override suspend fun searchPatients(searchParameters: SearchParameters): Flow<PagingData<PatientResponse>> {
        val searchList = searchDao.getPatientList()
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    getFuzzySearchList(
                        searchList,
                        searchParameters,
                        70
                    ), PAGE_SIZE
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { patientAndIdentifierEntity ->
                patientAndIdentifierEntity.toPatientResponse()
            }
        }
    }

    override suspend fun searchPatientByQuery(query: String): Flow<PagingData<PatientResponse>> {
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
}