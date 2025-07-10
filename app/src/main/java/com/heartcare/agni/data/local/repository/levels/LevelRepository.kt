package com.heartcare.agni.data.local.repository.levels

import com.heartcare.agni.data.server.model.levels.LevelResponse

interface LevelRepository {
    suspend fun insertLevel(vararg levelResponse: LevelResponse): List<Long>
    suspend fun getLevels(levelType: String, precedingId: String? = null): List<LevelResponse>
    suspend fun getLevelNameFromFhirId(fhirId: String): String
    suspend fun getLevelByFhirId(fhirId: String): LevelResponse
}