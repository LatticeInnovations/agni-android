package com.latticeonfhir.android.pagingsource

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.utils.constants.Paging
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientAndIdentifierEntityResponse
import com.latticeonfhir.android.utils.paging.SearchPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SearchPagingSourceTest : BaseClass() {

    private lateinit var searchPagingSource: SearchPagingSource

    private val patientList = listOf(
        patientResponse,
        patientResponse.copy(id = "ID_2"),
        patientResponse.copy(id = "ID_3"),
        patientResponse.copy(id = "ID_4"),
        patientResponse.copy(id = "ID_5"),
        patientResponse.copy(id = "ID_6"),
        patientResponse.copy(id = "ID_7"),
        patientResponse.copy(id = "ID_8"),
        patientResponse.copy(id = "ID_9"),
        patientResponse.copy(id = "ID_10"),
        patientResponse.copy(id = "ID_11"),
        patientResponse.copy(id = "ID_12"),
        patientResponse.copy(id = "ID_13"),
        patientResponse.copy(id = "ID_14"),
        patientResponse.copy(id = "ID_15")
    )

    @Before
    public override fun setUp() {
    }

    @Test
    fun `search paging source load - empty data`() = runTest {
        val patientList = emptyList<PatientAndIdentifierEntity>()
        searchPagingSource = SearchPagingSource(patientList, 10)
        val expectedResult = PagingSource.LoadResult.Page(
            data = patientList.subList(0, patientList.size),
            prevKey = null,
            nextKey = null
        )
        assertEquals(
            expectedResult, searchPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `search paging source load - Refresh data`() = runTest {
        val fuzzyList = patientList.map { it.toPatientAndIdentifierEntityResponse() }
        searchPagingSource = SearchPagingSource(fuzzyList, 10)
        val expectedResult = PagingSource.LoadResult.Page(
            data = fuzzyList.subList(0, 10),
            prevKey = null,
            nextKey = 1
        )
        assertEquals(
            expectedResult, searchPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `search paging source load - Append data`() = runTest {
        val fuzzyList = patientList.map { it.toPatientAndIdentifierEntityResponse() }
        searchPagingSource = SearchPagingSource(fuzzyList, 10)
        val expectedResult = PagingSource.LoadResult.Page(
            data = fuzzyList.subList(10, patientList.size),
            prevKey = 0,
            nextKey = null
        )
        assertEquals(
            expectedResult, searchPagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 1,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `search paging source load - Prepend data`() = runTest {
        val fuzzyList = patientList.map { it.toPatientAndIdentifierEntityResponse() }
        searchPagingSource = SearchPagingSource(fuzzyList, 10)
        val expectedResult = PagingSource.LoadResult.Page(
            data = fuzzyList.subList(0, 10),
            prevKey = null,
            nextKey = 1
        )
        assertEquals(
            expectedResult, searchPagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `search paging source load - Anchor Key`() = runTest {
        val fuzzyList = patientList.map { it.toPatientAndIdentifierEntityResponse() }
        searchPagingSource = SearchPagingSource(fuzzyList, 10)
        val expectedResult = 0
        assertEquals(
            expectedResult, searchPagingSource.getRefreshKey(
                PagingState(
                    listOf(
                        PagingSource.LoadResult.Page(
                            data = fuzzyList.subList(0, 10),
                            prevKey = null,
                            nextKey = 1
                        ),
                        PagingSource.LoadResult.Page(
                            data = fuzzyList.subList(10, patientList.size),
                            prevKey = 0,
                            nextKey = null
                        )
                    ),
                    1,
                    config = PagingConfig(
                        pageSize = Paging.PAGE_SIZE,
                        enablePlaceholders = false
                    ),
                    leadingPlaceholderCount = 0
                )
            )
        )
    }
}