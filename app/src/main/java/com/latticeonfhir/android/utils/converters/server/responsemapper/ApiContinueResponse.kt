package com.latticeonfhir.android.utils.converters.server.responsemapper

data class ApiContinueResponse<T>(val body: T) : ResponseMapper<T>()