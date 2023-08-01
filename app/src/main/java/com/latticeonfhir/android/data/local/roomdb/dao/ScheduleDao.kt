package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.schedule.ScheduleEntity
import java.util.Date

@Dao
interface ScheduleDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntity(vararg scheduleEntity: ScheduleEntity): List<Long>

    @Transaction
    @Query("UPDATE ScheduleEntity SET scheduleFhirId=:fhirId WHERE id=:id")
    suspend fun updateScheduleFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM ScheduleEntity WHERE id IN (:scheduleId)")
    suspend fun getScheduleById(vararg scheduleId: String): List<ScheduleEntity>

    @Transaction
    @Query("SELECT id FROM ScheduleEntity WHERE scheduleFhirId=:fhirId")
    suspend fun getScheduleIdByFhirId(fhirId: String): String?

    @Transaction
    @Query("SELECT bookedSlots FROM ScheduleEntity WHERE startTime=:start")
    suspend fun getBookedSlotsCountByStartTime(start: Date): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity): Int
}