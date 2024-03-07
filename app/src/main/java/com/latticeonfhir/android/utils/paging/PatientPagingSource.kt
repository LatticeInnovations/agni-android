package com.latticeonfhir.android.utils.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.utils.search.Search.getFuzzySearchPatientList
import org.hl7.fhir.r4.model.Patient
import java.io.IOException

/**
 * pageSize - Data to be displayed per page
 *
 * isSearching - Fuzzy Search
 *
 * searchParameters -> Search Parameters for fuzzy search
 *
 */
class PatientPagingSource(
    private val fhirEngine: FhirEngine,
    private val pageSize: Int,
    private val isSearching: Boolean,
    private val searchParameters: SearchParameters?
) : PagingSource<Int, Patient>() {

    override fun getRefreshKey(state: PagingState<Int, Patient>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(pageSize) ?: anchorPage?.nextKey?.minus(pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Patient> {
        return try {
            val position = params.key ?: 0
            val pendingData = if (isSearching) getPatientList(position).fuzzySearch()
                else getPatientList(position)
            LoadResult.Page (
                data = pendingData,
                prevKey = if (position == 0) null else position - pageSize,
                nextKey = if (pendingData.size < pageSize && !isSearching) null else position + pageSize
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    /**
     *
     * Fetch Patient List
     *
     */
    private suspend fun getPatientList(offset: Int): List<Patient> {
        return fhirEngine.search<Patient> {
            count = pageSize
            from = offset
        }.map {
            it.resource
        }
    }

    /**
     *
     * Fuzzy Search on Patient List
     *
     */
    private fun List<Patient>.fuzzySearch(): List<Patient> {
        return getFuzzySearchPatientList(this, searchParameters!!, 70)
    }
}