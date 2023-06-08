package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity

@Dao
interface SearchDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(searchHistoryEntity: SearchHistoryEntity): Long

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT searchQuery FROM SearchHistoryEntity WHERE searchType = :searchTypeEnum ORDER BY date ASC")
    suspend fun getRecentSearches(searchTypeEnum: SearchTypeEnum): List<String>

    @Transaction
    @Query("SELECT id FROM SearchHistoryEntity WHERE searchType = :searchTypeEnum ORDER BY date ASC LIMIT 1")
    suspend fun getOldestRecentSearchId(searchTypeEnum: SearchTypeEnum): Int

    @Transaction
    @Query("DELETE FROM SearchHistoryEntity WHERE id=:id")
    suspend fun deleteRecentSearch(id: Int): Int

    @Transaction
    @Query("SELECT DISTINCT activeIngredient FROM MedicationEntity")
    suspend fun getActiveIngredients(): List<String>
}