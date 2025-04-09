package com.latticeonfhir.android.data.local.repository.vaccination

import com.latticeonfhir.core.data.local.roomdb.entities.vaccination.ManufacturerEntity

interface ManufacturerRepository {

    suspend fun getAllManufacturers(): List<ManufacturerEntity>
}