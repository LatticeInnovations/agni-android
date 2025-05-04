package com.latticeonfhir.core.model.server.authentication

import androidx.annotation.Keep
import com.latticeonfhir.core.model.enums.RegisterTypeEnum

@Keep
data class Login(
    val userContact: String,
    val type: RegisterTypeEnum? = null
)
