package com.latticeonfhir.android.data.local.repository.cvd.chart

import com.latticeonfhir.android.data.local.roomdb.dao.RiskPredictionDao
import com.latticeonfhir.android.data.local.roomdb.entities.cvd.RiskPredictionCharts
import javax.inject.Inject

class RiskPredictionChartRepositoryImpl @Inject constructor(
    private val riskPredictionDao: RiskPredictionDao
) : RiskPredictionChartRepository {
    override suspend fun insertRecords(vararg riskPredictionCharts: RiskPredictionCharts): List<Long> {
        return riskPredictionDao.insertRecords(*riskPredictionCharts)
    }

    override suspend fun getRiskLevels(
        sex: String,
        tobaccoStatus: Int,
        age: Int,
        sys: Int,
        cholesterol: Double?,
        bmi: Double?,
        diabetes: Int
    ): String {
        return riskPredictionDao.predictRisk(
            sex, tobaccoStatus, age, sys, cholesterol, bmi, "sa", diabetes
        )
    }

}