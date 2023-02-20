package com.latticeonfhir.android.utils.converters.server.responsemapper

data class ApiErrorResponse<T>(val statusCode: Int, val errorMessage: String) : ResponseMapper<T>()
