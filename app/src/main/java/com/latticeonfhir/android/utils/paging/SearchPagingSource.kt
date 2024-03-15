package com.latticeonfhir.android.utils.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import org.hl7.fhir.r4.model.Patient

class SearchPagingSource(
    private val fuzzyList: List<Patient>,
    private val pageSize: Int
) : PagingSource<Int, Patient>() {

    override fun getRefreshKey(state: PagingState<Int, Patient>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Patient> {
        return try {
            val position = params.key ?: 0
            val left = position * pageSize
            val right =
                if ((position * pageSize) + pageSize < fuzzyList.size) (position * pageSize) + pageSize else fuzzyList.size
            LoadResult.Page(
                data = fuzzyList.subList(left, right),
                prevKey = if (position == 0) null else position - 1,
                nextKey = if ((right - left) != pageSize) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(
                e
            )
        }
    }
}