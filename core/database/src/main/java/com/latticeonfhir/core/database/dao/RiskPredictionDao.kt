package com.latticeonfhir.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.latticeonfhir.core.database.entities.cvd.RiskPredictionCharts

@Dao
interface RiskPredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecords(vararg riskPredictionCharts: RiskPredictionCharts): List<Long>

    @Query(
        "SELECT rpc.hsValue as value " +
                " FROM RiskPredictionCharts rpc" +
                " Where rpc.regionCode = :region " +
                " AND rpc.gender = :sex  " +
                " AND rpc.smoker = :tobaccoStatus  " +
                " AND rpc.age=(SELECT CASE WHEN :age<=44 THEN 40 WHEN :age between 45 and 49 THEN 45 WHEN :age between 50 and 54 THEN 50 WHEN :age between 55 and 59 THEN 55 WHEN :age between 60 and 64 THEN 60 WHEN :age between 65 and 69 THEN 65 WHEN :age>=70 THEN 70 END) " +
                " AND rpc.systolic=(SELECT CASE WHEN :sys <120 THEN 119 WHEN " +
                ":sys between 120 and 139 THEN 120 WHEN :sys between 140 and 159 THEN 140 WHEN :sys between 160 and 179 THEN 160 WHEN :sys >=180 THEN 180 END) " +
                " AND CASE WHEN :cholesterol IS NOT NULL AND rpc.diabetes = :diabetes THEN\n" +
                " rpc.cholesterol = (SELECT CASE WHEN :cholesterol <4 THEN 3.9 WHEN :cholesterol between 4 and 4.9 THEN 4 WHEN :cholesterol between 5 and 5.9 THEN 5 WHEN :cholesterol between 6 and 6.9 THEN 6 WHEN :cholesterol >=7 THEN 7 END) " +
                " WHEN :cholesterol IS NULL AND rpc.diabetes = '.' AND rpc.cholesterol = '.' THEN rpc.bmi = (SELECT CASE WHEN :bmi<20 THEN 19.9 WHEN :bmi between 20 and 24.9 THEN 20 WHEN :bmi between 25 AND 29.9 THEN 25 WHEN :bmi between 30 and 34.9 THEN 30 WHEN :bmi>=35 THEN 35 END)\n" +
                " END"
    )
    fun predictRisk(
        sex: String,
        tobaccoStatus: Int,
        age: Int,
        sys: Int,
        cholesterol: Double?,
        bmi: Double?,
        region: String,
        diabetes: Int
    ): String
}
