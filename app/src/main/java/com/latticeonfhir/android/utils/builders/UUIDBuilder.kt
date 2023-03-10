package com.latticeonfhir.android.utils.builders

import java.util.UUID

object UUIDBuilder {

    fun getUUID(): String {
        return "urn:uuid:${UUID.randomUUID()}"
    }
}