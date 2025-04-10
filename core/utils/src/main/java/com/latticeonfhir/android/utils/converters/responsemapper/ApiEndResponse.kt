package com.latticeonfhir.core.utils.converters.server.responsemapper

data class ApiEndResponse<T>(val body: T) : ResponseMapper<T>()
