package com.latticeonfhir.android.utils.converters.server.responsemapper

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.latticeonfhir.android.base.server.BaseResponse
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception

sealed class ResponseMapper<out T> {

    companion object {
        fun <T> create(response: Response<BaseResponse<T>>?, paginated: Boolean): ResponseMapper<T> {
            return if (response == null) {
                return ApiNullResponse
            } else if (response.isSuccessful) {
                if (response.body()?.status != 0) {
                    if (response.body()?.data == null) {
                        ApiEmptyResponse()
                    } else {
                        when {
                            paginated && response.body()?.status == 1 -> ApiContinueResponse(body = response.body()?.data!!)
                            response.body()?.status == 2 -> ApiEndResponse(body = response.body()?.data!!)
                            response.body()?.status == 1 -> ApiEndResponse(body = response.body()?.data!!)
                            else -> ApiErrorResponse(response.body()?.status ?: -1, response.body()?.message ?: "Server error")
                        }
                    }
                } else {
                    ApiErrorResponse(response.body()?.status ?: -1, response.body()?.message ?: "Server error")
                }
            } else {
                val gson = GsonBuilder().setPrettyPrinting().create();
                val collectionType = object : TypeToken<BaseResponse<T?>>() {}.type
                try {
                    val data: BaseResponse<Any?> =
                        gson.fromJson(response.errorBody()?.string(), collectionType)
                    ApiErrorResponse(response.code(), data.message)
                } catch (e: JsonSyntaxException) {
                    Timber.e(e)
                    ApiErrorResponse(0, "Server error")
                } catch (exception: Exception) {
                    Timber.e(exception)
                    ApiErrorResponse(0, exception.localizedMessage?.toString()?:"Server error")
                }
            }
        }
    }
}
