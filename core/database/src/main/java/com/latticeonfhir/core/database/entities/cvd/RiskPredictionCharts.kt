package com.latticeonfhir.core.database.entities.cvd

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class RiskPredictionCharts(
    @PrimaryKey var id: Int,
    @ColumnInfo var regionCode: String,
    @ColumnInfo var riskLevel: String? = null,
    @ColumnInfo var diabetes: String,
    @ColumnInfo var gender: String,
    @ColumnInfo var smoker: String,
    @ColumnInfo var age: Int,
    @ColumnInfo var systolic: String,
    @ColumnInfo var cholesterol: String,
    @ColumnInfo var cholesterolUnit: String,
    @ColumnInfo var bmi: String,
    @ColumnInfo var bmiUnit: String,
    @ColumnInfo var hs: String,
    @ColumnInfo var hsValue: String,
    @ColumnInfo var riskLevelId: Int
)
