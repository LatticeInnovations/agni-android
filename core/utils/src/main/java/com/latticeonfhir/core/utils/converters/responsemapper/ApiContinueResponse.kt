package com.latticeonfhir.core.utils.converters.responsemapper

data class ApiContinueResponse<T>(val body: T) : ResponseMapper<T>()