package com.latticeonfhir.android.utils.converters.serverresponse.responseconverter

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.server.model.PersonResponse
import java.util.UUID

fun PersonResponse.toGenericEntity(): GenericEntity {
    return GenericEntity(
        id = identifier?.get(0)?.identifierNumber ?: UUID.randomUUID().toString(),
        payload = FhirApp.gson.toJson(this),
        type = GenericTypeEnum.PERSON
    )
}

inline fun <reified T> GenericEntity.toPersonResponse(): T {
    return FhirApp.gson.fromJson(payload, T::class.java)
}
