package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PersonDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.model.PersonResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toGenericEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPersonEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val personDao: PersonDao,
    private val identifierDao: IdentifierDao
) : SyncRepository {

    override suspend fun getListPersonData(): ResponseMapper<List<PersonResponse>> {
        val response = ApiResponseConverter.convert(apiService.getListPersonData())
        if (response is ApiSuccessResponse) {
            personDao.insertListPersonData(response.body.map { personResponse ->
                personResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                    identifierDao.insertListOfIdentifier(listOfIdentifiers)
                }
                personResponse.toPersonEntity()
            })
        }
        if (response is ApiEndResponse) {
            personDao.insertListPersonData(response.body.map { personResponse ->
                personResponse.toListOfIdentifierEntity()?.let { listOfIdentifiers ->
                    identifierDao.insertListOfIdentifier(listOfIdentifiers)
                }
                personResponse.toPersonEntity()
            })
        }
        return response
    }

    override suspend fun getPersonDataById(id: String): ResponseMapper<List<PersonResponse>> {
        val response = ApiResponseConverter.convert(apiService.getPersonDataById(id))
        if (response is ApiSuccessResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toPersonEntity()
            })
        }
        if (response is ApiEndResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toPersonEntity()
            })
        }
        return response
    }
}