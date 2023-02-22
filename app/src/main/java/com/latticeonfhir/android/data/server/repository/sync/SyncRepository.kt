package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.server.model.PersonResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SyncRepository {

    suspend fun getListPersonData(): ResponseMapper<List<PersonResponse>>
    suspend fun getPersonDataById(id: String): ResponseMapper<List<PersonResponse>>
}