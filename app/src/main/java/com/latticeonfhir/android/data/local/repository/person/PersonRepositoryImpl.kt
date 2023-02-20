package com.latticeonfhir.android.data.local.repository.person

import com.latticeonfhir.android.data.local.roomdb.dao.PersonDao
import com.latticeonfhir.android.data.server.model.PersonResponse
import com.latticeonfhir.android.utils.converters.serverresponse.responseconverter.toPersonResponse
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(private val personDao: PersonDao): PersonRepository {

    override suspend fun getPersonList(): List<PersonResponse> {
        return personDao.getListPersonData().map {
            it.toPersonResponse()
        }
    }
}