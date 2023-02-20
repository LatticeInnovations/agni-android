package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.server.model.PersonResponse

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPersonData(list: List<GenericEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonData(personData: GenericEntity): Long

    @Query("SELECT * FROM GenericEntity WHERE type=:type")
    suspend fun getListPersonData(type: GenericTypeEnum = GenericTypeEnum.PERSON): List<GenericEntity>

    @Query("SELECT payload FROM GenericEntity WHERE id=:personId AND type=:type")
    suspend fun getPersonDataById(personId: String, type: GenericTypeEnum = GenericTypeEnum.PERSON): String
}