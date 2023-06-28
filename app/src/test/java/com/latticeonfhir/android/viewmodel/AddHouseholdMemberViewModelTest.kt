package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.householdmember.addhouseholdmember.AddHouseholdMemberViewModel
import com.latticeonfhir.android.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.LinkedList

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AddHouseholdMemberViewModelTest: BaseClass() {
    @Mock
    lateinit var searchRepository: SearchRepository
    lateinit var viewModel: AddHouseholdMemberViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    public override fun setUp(){
        MockitoAnnotations.initMocks(this)
        viewModel = AddHouseholdMemberViewModel(searchRepository)
    }

    @Test
    fun getSuggestionsTest(): Unit = runTest {
        val linkedList = LinkedList<PatientResponse>()
        `when`(searchRepository.getSuggestedMembers(patientResponse.id, SearchParameters(
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
        ){}).thenReturn(Unit)
        viewModel.getSuggestions(patientResponse){
            val actual = it
            assertEquals(0, actual.size)
        }
    }
}