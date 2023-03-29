package com.latticeonfhir.android.data.local.repository.search

import com.latticeonfhir.android.data.server.model.patient.PatientResponse

interface SearchRepository {

    suspend fun searchPatients(query: String): List<String>
}