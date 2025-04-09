package com.latticeonfhir.core.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.core.data.local.enums.SearchTypeEnum
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.core.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.core.data.local.roomdb.entities.search.SymDiagSearchEntity
import com.latticeonfhir.core.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.core.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsEntity

@Dao
interface SearchDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(searchHistoryEntity: SearchHistoryEntity): Long

    @Transaction
    @Query("SELECT * FROM PatientEntity INNER JOIN PatientLastUpdatedEntity ON PatientEntity.id = PatientLastUpdatedEntity.patientId ORDER BY PatientLastUpdatedEntity.lastUpdated DESC")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT searchQuery FROM SearchHistoryEntity WHERE searchType = :searchTypeEnum ORDER BY date DESC")
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

    @Transaction
    @Query("SELECT * FROM symptoms")
    suspend fun getSymptoms(): List<SymptomsEntity>

    @Transaction
    @Query("SELECT * FROM diagnosis")
    suspend fun getDiagnosis(): List<DiagnosisEntity>

    // Insert a new search entry or update the existing one
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSearch(symDiagSearchEntity: SymDiagSearchEntity): Long

    // Get the most frequent searches (limit to 5)
    @Query("SELECT searchQuery FROM SymDiagSearchEntity where searchType=:searchTypeEnum ORDER BY searchCount DESC LIMIT 5")
    suspend fun getMostFrequentSearches(searchTypeEnum: SearchTypeEnum): List<String>

    // Check if a query already exists
    @Query("SELECT * FROM SymDiagSearchEntity WHERE searchQuery = :searchQuery")
    suspend fun getSearchByQuery(searchQuery: String): SymDiagSearchEntity?

    // Delete the oldest record (the one with the lowest `id`)
    @Query("DELETE FROM SymDiagSearchEntity WHERE id = (SELECT MIN(id) FROM SymDiagSearchEntity)")
    suspend fun deleteOldestEntry()

    // Get the count of records in the table
    @Query("SELECT COUNT(*) FROM SymDiagSearchEntity")
    suspend fun getRowCount(): Int

    @Query("UPDATE SymDiagSearchEntity Set searchCount=:searchCount where searchQuery=:searchQuery")
    suspend fun updateSearch(searchCount: Long, searchQuery: String): Int

}