package com.latticeonfhir.android.data.local.repository.search

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface SearchRepository {

    suspend fun searchPatients(searchParameters: SearchParameters): LiveData<PagingData<PatientResponse>>
}