package com.latticeonfhir.android.utils.converters.serverresponse.responsemapper

data class ApiEndResponse<T>(val body: T) : ResponseMapper<T>()
