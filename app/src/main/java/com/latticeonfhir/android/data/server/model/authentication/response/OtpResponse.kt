package com.latticeonfhir.android.data.server.model.authentication.response

import androidx.annotation.Keep

@Keep
data class OtpResponse(
    val orgId: String,
    val roles: List<String>,
    val token: String,
    val userId: String,
    val username: String,
    val contactNumber: String,
    val sessionId: String,
    val locationId: List<LocationReference>
)