package com.latticeonfhir.core.utils.converters.server.responsemapper

data class ApiErrorResponse<T>(val statusCode: Int, val errorMessage: String) : ResponseMapper<T>()
