package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface LevelsApiService {

    @GET("level")
    suspend fun getLevelsData(
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<LevelResponse>>>

}