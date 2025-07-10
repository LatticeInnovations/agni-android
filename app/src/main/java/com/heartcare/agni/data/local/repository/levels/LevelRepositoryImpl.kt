package com.heartcare.agni.data.local.repository.levels

import com.heartcare.agni.data.local.roomdb.dao.LevelsDao
import com.heartcare.agni.data.server.model.levels.LevelResponse
import javax.inject.Inject
import com.heartcare.agni.utils.converters.responseconverter.toLevelEntity
import com.heartcare.agni.utils.converters.responseconverter.toLevelResponse

class LevelRepositoryImpl @Inject constructor(
    private val levelsDao: LevelsDao
): LevelRepository {
    override suspend fun insertLevel(vararg levelResponse: LevelResponse): List<Long> {
        return levelsDao.insertLevelEntity(*levelResponse.map { it.toLevelEntity()}.toTypedArray())
    }

    override suspend fun getLevels(levelType: String, precedingId: String?): List<LevelResponse> {
        return levelsDao.getLevels(levelType, precedingId)
            .map { it.toLevelResponse() }
    }

    override suspend fun getLevelNameFromFhirId(fhirId: String): String {
        return levelsDao.getLevelNameFromFhirId(fhirId)
    }
}