package com.latticeonfhir.android.data.local.repository.search

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface SearchRepository {

    suspend fun searchPatients(query: String): LiveData<PagingData<PatientResponse>>
}