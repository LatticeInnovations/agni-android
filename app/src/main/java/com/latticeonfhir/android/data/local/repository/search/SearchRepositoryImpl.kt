package com.latticeonfhir.android.data.local.repository.search

import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(private val searchDao: SearchDao): SearchRepository {

    override suspend fun searchPatients(query: String): List<String> {
        val patientSet = mutableSetOf<String>()
        searchDao.getPatientList().forEach { patientAndIdentifierEntity ->
            if(FuzzySearch.ratio(query,patientAndIdentifierEntity.patientEntity.firstName) > 70) patientSet.add(patientAndIdentifierEntity.patientEntity.firstName)
        }
        return patientSet.toList()
    }
}