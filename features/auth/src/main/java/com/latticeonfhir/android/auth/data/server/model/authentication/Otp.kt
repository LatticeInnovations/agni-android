package com.latticeonfhir.core.auth.data.server.model.authentication

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum

@Keep
data class Otp(
    val userContact: String,
    val otp: Int,
    val type: RegisterTypeEnum? = null
)
