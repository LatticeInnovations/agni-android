package com.latticeonfhir.android.data.server.model.user

import androidx.annotation.Keep

@Keep
data class UserRoleDetails(
    val roleId: String,
    val role: String,
    val orgName: String,
    val orgId: String
)
