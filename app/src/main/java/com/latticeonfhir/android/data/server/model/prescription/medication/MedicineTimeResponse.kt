package com.latticeonfhir.android.data.server.model.prescription.medication

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MedicineTimeResponse(
    @SerializedName("medinstructionVal")
    val medInstructionVal: String,
    @SerializedName("medinstructionCode")
    val medInstructionCode: String
)
