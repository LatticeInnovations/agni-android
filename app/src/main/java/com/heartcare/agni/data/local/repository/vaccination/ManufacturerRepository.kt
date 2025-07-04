package com.heartcare.agni.data.local.repository.vaccination

import com.heartcare.agni.data.local.roomdb.entities.vaccination.ManufacturerEntity

interface ManufacturerRepository {

    suspend fun getAllManufacturers(): List<ManufacturerEntity>
}