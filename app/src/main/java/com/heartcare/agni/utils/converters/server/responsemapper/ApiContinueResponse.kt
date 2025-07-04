package com.heartcare.agni.utils.converters.server.responsemapper

data class ApiContinueResponse<T>(val body: T) : ResponseMapper<T>()