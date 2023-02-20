package com.latticeonfhir.android.utils.converters.server.responsemapper

data class ApiSuccessResponse<T>(val body: T) : ResponseMapper<T>()