package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.SearchHistoryEntity

@Dao
interface SearchDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(searchHistoryEntity: SearchHistoryEntity): Long

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT searchQuery FROM SearchHistoryEntity ORDER BY date ASC")
    suspend fun getRecentSearches(): List<String>

    @Transaction
    @Query("SELECT id FROM SearchHistoryEntity ORDER BY date ASC LIMIT 1")
    suspend fun getOldestRecentSearchId(): Int

    @Transaction
    @Query("DELETE FROM SearchHistoryEntity WHERE id=:id")
    suspend fun deleteRecentSearch(id: Int): Int
}