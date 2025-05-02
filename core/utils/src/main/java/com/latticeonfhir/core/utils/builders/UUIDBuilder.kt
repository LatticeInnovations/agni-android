package com.latticeonfhir.core.utils.builders

import java.util.UUID

object UUIDBuilder {

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}