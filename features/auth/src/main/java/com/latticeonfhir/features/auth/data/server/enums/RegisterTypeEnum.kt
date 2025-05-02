package com.latticeonfhir.features.auth.data.server.enums

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class RegisterTypeEnum {
    @SerializedName("register")
    REGISTER,

    @SerializedName("delete")
    DELETE
}