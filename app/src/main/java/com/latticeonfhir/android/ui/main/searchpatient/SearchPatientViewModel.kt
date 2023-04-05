package com.latticeonfhir.android.ui.main.searchpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.main.patientregistration.step3.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class SearchPatientViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var step by mutableStateOf(1)
    var patientName by mutableStateOf("")
    var patientId by mutableStateOf("")
    var gender by mutableStateOf("")
    var minAge by mutableStateOf("20")
    var maxAge by mutableStateOf("40")
    val visitIntervals =
        listOf("Last week", "Last month", "Last 3 months", "Last year")
    var visitSelected by mutableStateOf(visitIntervals[0])

    var range by mutableStateOf(minAge.toFloat()..maxAge.toFloat())

    var isNameValid by mutableStateOf(false)
    var isPatientIdValid by mutableStateOf(false)

    var address = Address()

    fun updateRange(minAge: String, maxAge: String){
        val min: String
        val max: String
        if (minAge.isEmpty()) min = "0" else min = minAge
        if (maxAge.isEmpty()) max = "0" else max = maxAge
        range = min.toFloat()..max.toFloat()
    }
}