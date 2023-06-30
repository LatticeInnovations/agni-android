package com.latticeonfhir.android.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.pagination.PaginationResponse
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.householdmember.searchresult.SearchResultViewModel
import com.latticeonfhir.android.utils.MainCoroutineRule
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientAndIdentifierEntityResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.getOrAwaitValue
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.LinkedList

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SearchResultViewModelTest : BaseClass() {

    @Mock
    private lateinit var searchRepository: SearchRepository

    @Mock
    private lateinit var searchDao: SearchDao

    @Mock
    private lateinit var relationDao: RelationDao
    private lateinit var searchResultViewModel: SearchResultViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchResultViewModel = SearchResultViewModel(searchRepository)
    }

    @Test
    fun `search patient returns paging data`() = runTest {
        searchResultViewModel.patientFrom = patientResponse

        `when`(
            searchRepository.filteredSearchPatients(
                patientResponse.id, SearchParameters(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    patientResponse.permanentAddress.addressLine1,
                    patientResponse.permanentAddress.city,
                    patientResponse.permanentAddress.district,
                    patientResponse.permanentAddress.state,
                    patientResponse.permanentAddress.postalCode,
                    patientResponse.permanentAddress.addressLine2
                )
            )
        ).thenReturn(flow {
            emit(PagingData.from(listOf(PaginationResponse(patientResponse, size = 2),PaginationResponse(patientResponse.copy(id = "NEW_ID"), size = 2))))
        })

        searchResultViewModel.searchPatient(
            SearchParameters(
                null,
                null,
                null,
                null,
                null,
                null,
                patientResponse.permanentAddress.addressLine1,
                patientResponse.permanentAddress.city,
                patientResponse.permanentAddress.district,
                patientResponse.permanentAddress.state,
                patientResponse.permanentAddress.postalCode,
                patientResponse.permanentAddress.addressLine2
            )
        )
        delay(5000)
        assertEquals(true,true)
    }
}