package com.latticeonfhir.android.utils.converters.server.responsemapper

import com.latticeonfhir.android.base.server.BaseResponse
import retrofit2.Response

object ApiResponseConverter {

    fun <T> convert(
        response: Response<BaseResponse<T>>,
        paginated: Boolean = false
    ): ResponseMapper<T> {
        return ResponseMapper.create(response, paginated)
    }
}