package com.latticeonfhir.android.data.local.repository.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchDao: SearchDao) :
    SearchRepository {

    override suspend fun searchPatients(query: String): LiveData<PagingData<PatientResponse>> {
        val patientSet = mutableSetOf<PatientAndIdentifierEntity>()
        searchDao.getPatientList().forEach { patientAndIdentifierEntity ->
            if (FuzzySearch.ratio(
                    query,
                    patientAndIdentifierEntity.patientEntity.firstName
                ) > 70
            ) patientSet.add(patientAndIdentifierEntity)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                maxSize = PAGE_SIZE
            ),
            pagingSourceFactory = { SearchPagingSource(patientSet.toList(), PAGE_SIZE) }
        ).liveData.map { pagingData ->
            pagingData.map { patientAndIdentifierEntity ->
                patientAndIdentifierEntity.toPatientResponse()
            }
        }
    }
}