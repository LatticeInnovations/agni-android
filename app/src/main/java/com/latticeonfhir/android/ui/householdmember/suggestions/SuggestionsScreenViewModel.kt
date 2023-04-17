package com.latticeonfhir.android.ui.householdmember.suggestions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.SynchronousQueue
import javax.inject.Inject

@HiltViewModel
class SuggestionsScreenViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    BaseViewModel() {
    var showConnectDialog by mutableStateOf(false)

    private val queue = SynchronousQueue<PatientResponse>()
    private var i = 0
    private lateinit var listOfSuggestions: List<PatientResponse>

    val patient = PatientResponse(
        id = "d138ada3-82f7-4b96-914f-decd5933b61d",
        firstName = "Mansi",
        middleName = null,
        lastName = "Kalra",
        active = true,
        birthDate = "2001-01-23",
        email = null,
        fhirId = null,
        gender = "female",
        mobileNumber = 9999999999,
        permanentAddress = PatientAddressResponse(
            addressLine1 = "hbghhg",
            addressLine2 = null,
            postalCode = "999999",
            city = "vggh",
            country = "India",
            district = null,
            state = "Uttarakhand"
        ),
        identifier = listOf(
            PatientIdentifier(
                code = null,
                identifierType = "https://www.apollohospitals.com/",
                identifierNumber = "XXXXXXXXXX"
            )
        )
    )

    val suggestedMembersList = listOf(patient, patient, patient)

    //var suggestedMembersList by mutableStateOf(listOf<PatientResponse>())

    internal fun getQueueItems() {
        while (queue.size < 5) {
            queue.add(
                listOfSuggestions[i]
            )
            i++
        }
    }

    internal fun updateQueue() {
        queue.poll()
        queue.offer(listOfSuggestions[i])
        i++
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            listOfSuggestions = searchRepository.getSuggestedMembers(
                patient.id,
                SearchParameters("")
            )
        }
    }
}