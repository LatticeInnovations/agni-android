package com.latticeonfhir.core.utils.converters.responsemapper

data class ApiErrorResponse<T>(val statusCode: Int, val errorMessage: String) : ResponseMapper<T>()
