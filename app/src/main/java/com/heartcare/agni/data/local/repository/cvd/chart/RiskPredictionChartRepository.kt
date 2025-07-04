package com.heartcare.agni.data.local.repository.cvd.chart

import com.heartcare.agni.data.local.roomdb.entities.cvd.RiskPredictionCharts

interface RiskPredictionChartRepository {
    suspend fun insertRecords(vararg riskPredictionCharts: RiskPredictionCharts): List<Long>
    suspend fun getRiskLevels(sex:String , tobaccoStatus : Int,age :Int, sys:Int , cholesterol:Double?, bmi:Double?,diabetes : Int): String
}