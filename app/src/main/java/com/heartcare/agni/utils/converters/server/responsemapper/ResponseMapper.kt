package com.heartcare.agni.utils.converters.server.responsemapper

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.utils.constants.ErrorConstants.SERVER_ERROR
import retrofit2.Response
import timber.log.Timber

sealed class ResponseMapper<out T> {

    companion object {

        fun <T> create(error: Throwable?): ApiErrorResponse<T> {
            return ApiErrorResponse(0, error?.message ?: SERVER_ERROR)
        }

        fun <T> create(
            response: Response<BaseResponse<T>>,
            paginated: Boolean
        ): ResponseMapper<T> {
            return if (response.isSuccessful) {
                mapData(response, paginated)
            } else {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val collectionType = object : TypeToken<BaseResponse<T?>>() {}.type
                try {
                    val data: BaseResponse<Any?> =
                        gson.fromJson(response.errorBody()?.string(), collectionType)
                    ApiErrorResponse(response.code(), data.message)
                } catch (e: JsonSyntaxException) {
                    Timber.e(e)
                    ApiErrorResponse(0, SERVER_ERROR)
                }
            }
        }

        private fun <T> mapData(
            response: Response<BaseResponse<T>>,
            paginated: Boolean
        ): ResponseMapper<T> {
            return if (response.body()?.status != 0) {
                if (response.body()?.data == null) {
                    ApiEmptyResponse()
                } else {
                    when {
                        paginated && response.body()?.status == 1 -> ApiContinueResponse(body = response.body()?.data!!)
                        paginated && response.body()?.status == 2 -> ApiEndResponse(body = response.body()?.data!!)
                        !paginated && response.body()?.status == 1 -> ApiEndResponse(body = response.body()?.data!!)
                        else -> ApiErrorResponse(
                            response.body()?.status ?: 0,
                            response.body()?.message ?: SERVER_ERROR
                        )
                    }
                }
            } else {
                ApiErrorResponse(
                    response.body()?.status ?: 0,
                    response.body()?.message ?: SERVER_ERROR
                )
            }
        }
    }
}
