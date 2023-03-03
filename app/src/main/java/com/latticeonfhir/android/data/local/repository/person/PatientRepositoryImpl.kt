package com.latticeonfhir.android.data.local.repository.person

import com.latticeonfhir.android.data.local.roomdb.dao.PersonDao
import com.latticeonfhir.android.data.server.model.PersonResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPersonEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPersonResponse
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(private val personDao: PersonDao): PatientRepository {

    override suspend fun getPersonList(): List<PersonResponse> {
        return personDao.getListPersonData().map {
            it.toPersonResponse()
        }
    }

    override suspend fun updatePersonData(personResponse: PersonResponse): Int {
        return personDao.updatePersonData(personResponse.toPersonEntity())
    }
}