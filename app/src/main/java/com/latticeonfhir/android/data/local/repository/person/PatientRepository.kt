package com.latticeonfhir.android.data.local.repository.person

import com.latticeonfhir.android.data.server.model.PersonResponse

interface PatientRepository {

    suspend fun getPersonList(): List<PersonResponse>
    suspend fun updatePersonData(personResponse: PersonResponse): Int
}