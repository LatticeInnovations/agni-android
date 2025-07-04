package com.heartcare.agni.utils.converters.server.responsemapper

import com.heartcare.agni.base.server.BaseResponse
import retrofit2.Response

object ApiResponseConverter {

    fun <T> convert(
        response: Response<BaseResponse<T>>,
        paginated: Boolean = false
    ): ResponseMapper<T> {
        return ResponseMapper.create(response, paginated)
    }
}