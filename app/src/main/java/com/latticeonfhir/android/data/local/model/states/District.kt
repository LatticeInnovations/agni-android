package com.latticeonfhir.android.data.local.model.states

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class District(
    val blocks: List<Block>,
    @SerializedName("district_name")
    val districtName: String,
    val pincodes: List<String>
)