package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.ui.householdmember.addhouseholdmember.AddHouseholdMemberViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.LinkedList

class AddHouseholdMemberViewModelTest: BaseClass() {
    @Mock
    lateinit var searchRepository: SearchRepository
    lateinit var viewModel: AddHouseholdMemberViewModel
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    public override fun setUp(){
        MockitoAnnotations.initMocks(this)
        viewModel = AddHouseholdMemberViewModel(searchRepository)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun getSuggestionsTest(): Unit = runBlocking {
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
        launch(Dispatchers.Main) {
            viewModel.getSuggestions(patientResponse){
                val actual = it
                Assert.assertEquals(listOf(relative), actual)
            }
        }
    }
}