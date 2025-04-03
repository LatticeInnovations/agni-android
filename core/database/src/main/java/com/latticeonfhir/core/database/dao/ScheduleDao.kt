package com.latticeonfhir.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.core.database.entities.schedule.ScheduleEntity
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
    @Query("SELECT startTime FROM ScheduleEntity WHERE scheduleFhirId=:fhirId")
    suspend fun getScheduleStartTimeByFhirId(fhirId: String): Date?

    @Transaction
    @Query("SELECT scheduleFhirId FROM ScheduleEntity WHERE startTime=:startTime")
    suspend fun getFhirIdByStartTime(startTime: Date): String?

    @Transaction
    @Query("SELECT COALESCE((SELECT bookedSlots FROM ScheduleEntity WHERE startTime=:start), :defaultValue)")
    suspend fun getBookedSlotsCountByStartTime(start: Long, defaultValue: Int = 0): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity): Int

    @Transaction
    @Query("SELECT * FROM ScheduleEntity WHERE startTime=:startTime")
    suspend fun getScheduleByStartTime(startTime: Long): ScheduleEntity?
}