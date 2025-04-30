package com.latticeonfhir.core.utils.converters.responsemapper

import com.latticeonfhir.core.model.base.BaseResponse
import retrofit2.Response

object ApiResponseConverter {

    fun <T> convert(
        response: Response<BaseResponse<T>>,
        paginated: Boolean = false
    ): ResponseMapper<T> {
        return ResponseMapper.create(response, paginated)
    }
}