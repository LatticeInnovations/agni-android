package com.latticeonfhir.android.utils.builders

import java.util.UUID

object UUIDBuilder {

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}