package com.latticeonfhir.core.utils.converters.responsemapper

data class ApiEndResponse<T>(val body: T) : ResponseMapper<T>()
