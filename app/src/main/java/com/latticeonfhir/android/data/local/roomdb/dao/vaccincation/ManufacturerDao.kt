package com.latticeonfhir.core.data.local.roomdb.dao.vaccincation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.core.data.local.roomdb.entities.vaccination.ManufacturerEntity

@Dao
interface ManufacturerDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManufacturer(vararg manufacturerEntity: ManufacturerEntity): List<Long>

    @Query("SELECT * FROM ManufacturerEntity")
    suspend fun getAllManufacturers(): List<ManufacturerEntity>

    @Query("SELECT * FROM ManufacturerEntity WHERE id = :id")
    suspend fun getManufacturerById(id: String): ManufacturerEntity
}