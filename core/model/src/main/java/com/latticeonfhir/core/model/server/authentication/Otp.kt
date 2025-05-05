package com.latticeonfhir.core.model.server.authentication

import androidx.annotation.Keep
import com.latticeonfhir.core.model.enums.RegisterTypeEnum

@Keep
data class Otp(
    val userContact: String,
    val otp: Int,
    val type: RegisterTypeEnum? = null
)
