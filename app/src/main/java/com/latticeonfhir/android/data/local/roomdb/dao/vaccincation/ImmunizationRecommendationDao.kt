package com.latticeonfhir.android.data.local.roomdb.dao.vaccincation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.vaccination.ImmunizationRecommendationEntity

@Dao
interface ImmunizationRecommendationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImmunizationRecommendation(vararg immunizationRecommendationEntity: ImmunizationRecommendationEntity): List<Long>

    @Query("SELECT * FROM ImmunizationRecommendationEntity WHERE patientId = :patientId")
    suspend fun getImmunizationRecommendationByPatientId(patientId: String): List<ImmunizationRecommendationEntity>

    @Query("SELECT * FROM ImmunizationRecommendationEntity WHERE vaccineCode = :vaccineCode LIMIT 1")
    suspend fun getImmunizationRecommendationByVaccineCode(vaccineCode: String): ImmunizationRecommendationEntity
}