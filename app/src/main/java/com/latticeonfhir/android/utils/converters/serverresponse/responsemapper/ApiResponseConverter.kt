package com.latticeonfhir.android.utils.converters.serverresponse.responsemapper

import com.latticeonfhir.android.base.server.BaseResponse
import retrofit2.Response
import timber.log.Timber

object ApiResponseConverter {

    fun <T> convert(response: Response<BaseResponse<T>>, paginated:Boolean=false): ResponseMapper<T> {
        return if(response.isSuccessful){
            Timber.e(response.toString())
            ResponseMapper.create(response,paginated)
        }else {
            ResponseMapper.create(Throwable("Server Error"))
        }
    }
}