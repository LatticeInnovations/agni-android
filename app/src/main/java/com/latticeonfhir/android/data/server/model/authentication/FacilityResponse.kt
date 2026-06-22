package com.latticeonfhir.android.data.server.model.authentication

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FacilityResponse(
    @SerializedName("facility_id")
    val facilityId: Int,
    val name: String,
    val block: String,
    @SerializedName("block_code")
    val blockCode: String?,
    @SerializedName("dist_code")
    val districtCode: String,
    val district: String,
    val state: String,
    @SerializedName("state_code")
    val stateCode: String
)