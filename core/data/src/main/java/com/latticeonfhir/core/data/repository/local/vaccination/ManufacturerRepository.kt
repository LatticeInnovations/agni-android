package com.latticeonfhir.core.data.repository.local.vaccination

import com.latticeonfhir.core.data.local.roomdb.entities.vaccination.ManufacturerEntity

interface ManufacturerRepository {

    suspend fun getAllManufacturers(): List<ManufacturerEntity>
}