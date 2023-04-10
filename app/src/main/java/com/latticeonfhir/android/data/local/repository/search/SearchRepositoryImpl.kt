package com.latticeonfhir.android.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchDao: SearchDao) :
    SearchRepository {

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
}