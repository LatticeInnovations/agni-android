package com.latticeonfhir.android.ui.landingscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel() {

    var isLaunched by mutableStateOf(false)
    val items = listOf("My Patients", "Queue", "Profile")
    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedIndex by mutableStateOf(0)
    lateinit var patientList: Flow<PagingData<PatientResponse>>
    lateinit var lazyPatientsList: Flow<PagingData<PatientResponse>>

    fun getPatientList() {
        viewModelScope.launch {
            patientList = patientRepository.getPatientList().asFlow().cachedIn(viewModelScope)
        }
    }

    // dummy data for search list
    val patientResponse = PatientResponse(
        id = "43cf9743-7844-4814-9b11-39dfc83faaa9",
        firstName = "search",
        middleName = null,
        lastName = "result",
        active = true,
        birthDate = Date().time.toPatientDate(),
        email = null,
        fhirId = null,
        gender = "male",
        mobileNumber = 99999999999,
        permanentAddress = PatientAddressResponse(
            addressLine1 = "something",
            addressLine2 = null,
            city = "somecity",
            district = null,
            state = "Uttarakhand",
            postalCode = "333333",
            country = "India"
        ),
        identifier = listOf(
            PatientIdentifier(
                code = null,
                identifierNumber = "XXXXXXXXXX",
                identifierType = "http://hospital.smarthealthit.org"
            )
        )
    )

    val searchResultList = flowOf(PagingData.from(listOf(patientResponse, patientResponse, patientResponse)))

    fun populateList(){
        if(isSearchResult){
            lazyPatientsList = searchResultList
        } else{
            getPatientList()
            lazyPatientsList = patientList
        }
    }
}