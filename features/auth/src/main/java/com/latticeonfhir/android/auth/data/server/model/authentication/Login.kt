package com.latticeonfhir.android.auth.data.server.model.authentication

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum

@Keep
data class Login(
    val userContact: String,
    val type: RegisterTypeEnum? = null
)
