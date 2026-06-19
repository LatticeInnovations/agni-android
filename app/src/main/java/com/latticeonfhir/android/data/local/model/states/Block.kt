package com.latticeonfhir.android.data.local.model.states

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Block(
    @SerializedName("block_code")
    val blockCode: String,
    @SerializedName("block_name")
    val blockName: String
)