package com.latticeonfhir.android.data.local.repository.person

import com.latticeonfhir.android.data.server.model.PersonResponse

interface PersonRepository {

    suspend fun getPersonList(): List<PersonResponse>
}