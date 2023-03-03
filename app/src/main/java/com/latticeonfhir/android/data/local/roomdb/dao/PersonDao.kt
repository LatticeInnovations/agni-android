package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PersonAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity

@Dao
interface PersonDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPersonData(list: List<PatientEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfIdentifiers(vararg listOfIdentifiers: IdentifierEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonData(patientEntity: PatientEntity): Long

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    suspend fun getListPersonData(): List<PersonAndIdentifierEntity>

    @Transaction
    @Query("SELECT * FROM PatientEntity WHERE id=:personId")
    suspend fun getPersonDataById(personId: String): PersonAndIdentifierEntity

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePersonData(patientEntity: PatientEntity): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListOfIdentifiers(vararg listOfIdentifiers: IdentifierEntity)
}