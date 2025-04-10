package com.latticeonfhir.core.data.local.repository.vaccination.impl

import com.latticeonfhir.android.data.local.repository.vaccination.ManufacturerRepository
import com.latticeonfhir.core.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.core.data.local.roomdb.entities.vaccination.ManufacturerEntity
import javax.inject.Inject

class ManufacturerRepositoryImpl @Inject constructor(private val manufacturerDao: ManufacturerDao): ManufacturerRepository {

    override suspend fun getAllManufacturers(): List<ManufacturerEntity> {
        return manufacturerDao.getAllManufacturers()
    }
}