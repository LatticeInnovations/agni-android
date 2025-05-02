package com.latticeonfhir.core.data.repository.local.vaccination.impl

import com.latticeonfhir.core.data.repository.local.vaccination.ManufacturerRepository
import com.latticeonfhir.core.database.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.core.database.entities.vaccination.ManufacturerEntity
import javax.inject.Inject

class ManufacturerRepositoryImpl @Inject constructor(private val manufacturerDao: ManufacturerDao): ManufacturerRepository {

    override suspend fun getAllManufacturers(): List<ManufacturerEntity> {
        return manufacturerDao.getAllManufacturers()
    }
}