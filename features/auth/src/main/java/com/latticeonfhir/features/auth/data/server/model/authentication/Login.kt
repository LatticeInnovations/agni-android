package com.latticeonfhir.features.auth.data.server.model.authentication

import androidx.annotation.Keep
import com.latticeonfhir.features.auth.data.server.enums.RegisterTypeEnum

@Keep
data class Login(
    val userContact: String,
    val type: RegisterTypeEnum? = null
)
