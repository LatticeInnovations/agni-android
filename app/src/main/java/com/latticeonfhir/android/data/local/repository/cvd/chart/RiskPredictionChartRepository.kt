package com.latticeonfhir.android.data.local.repository.cvd.chart

import com.latticeonfhir.android.data.local.roomdb.entities.cvd.RiskPredictionCharts

interface RiskPredictionChartRepository {
    suspend fun insertRecords(vararg riskPredictionCharts: RiskPredictionCharts): List<Long>
    suspend fun getRiskLevels(sex:String , tobaccoStatus : Int,age :Int, sys:Int , cholesterol:Double?, bmi:Double,diabetes : Int): String
}