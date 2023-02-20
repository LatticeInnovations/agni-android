package com.latticeonfhir.android.data.server.repository.sync

import com.latticeonfhir.android.data.local.roomdb.dao.PersonDao
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.data.server.model.PersonResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toGenericEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiSuccessResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val personDao: PersonDao
) : SyncRepository {

    override suspend fun getListPersonData(): ResponseMapper<List<PersonResponse>> {
        val response = ApiResponseConverter.convert(apiService.getListPersonData())
        if (response is ApiSuccessResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toGenericEntity()
            })
        }
        if (response is ApiEndResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toGenericEntity()
            })
        }
        return response
    }

    override suspend fun getPersonDataById(id: String): ResponseMapper<List<PersonResponse>> {
        val response = ApiResponseConverter.convert(apiService.getPersonDataById(id))
        if (response is ApiSuccessResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toGenericEntity()
            })
        }
        if (response is ApiEndResponse) {
            personDao.insertListPersonData(response.body.map {
                it.toGenericEntity()
            })
        }
        return response
    }
}