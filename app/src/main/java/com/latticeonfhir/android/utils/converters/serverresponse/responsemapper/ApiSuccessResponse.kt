package com.latticeonfhir.android.utils.converters.serverresponse.responsemapper

data class ApiSuccessResponse<T>(val body: T) : ResponseMapper<T>()