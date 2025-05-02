package com.latticeonfhir.core.data.repository.local.vaccination

import com.latticeonfhir.core.database.entities.vaccination.ManufacturerEntity

interface ManufacturerRepository {

    suspend fun getAllManufacturers(): List<ManufacturerEntity>
}