package com.latticeonfhir.utils.converters.responsemapper

import com.latticeonfhir.core.auth.data.server.model.BaseResponse
import com.latticeonfhir.core.utils.converters.server.responsemapper.ResponseMapper
import retrofit2.Response

object ApiResponseConverter {

    fun <T> convert(
        response: Response<BaseResponse<T>>,
        paginated: Boolean = false
    ): ResponseMapper<T> {
        return ResponseMapper.create(response, paginated)
    }
}