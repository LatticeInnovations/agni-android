package com.latticeonfhir.android.data.server.enums

import com.google.gson.annotations.SerializedName

enum class RegisterTypeEnum {
    @SerializedName("register")
    REGISTER,
    @SerializedName("delete")
    DELETE
}