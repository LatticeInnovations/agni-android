package com.latticeonfhir.android.data.server.model.prescription.medication

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MedicineTimeResponse(
    @SerializedName("medinstructionVal")
    val medInstructionVal: String,
    @SerializedName("medinstructionCode")
    val medInstructionCode: String
)
