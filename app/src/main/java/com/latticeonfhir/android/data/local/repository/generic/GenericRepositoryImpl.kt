package com.latticeonfhir.android.data.local.repository.generic

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import javax.inject.Inject

class GenericRepositoryImpl @Inject constructor(private val genericDao: GenericDao) : GenericRepository {

    override suspend fun insertGenericEntity(id: String, map: Map<String, ChangeRequest>): Long {
        val payload = genericDao.getChangeRequestPayload(id)
        if (payload != null) {
            FhirApp.gson.fromJson(payload, Map<String, ChangeRequest>::class.java)
        }
    }

}