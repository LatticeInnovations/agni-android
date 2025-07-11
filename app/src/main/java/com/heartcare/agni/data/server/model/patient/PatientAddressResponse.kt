package com.heartcare.agni.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PatientAddressResponse(
    @SerializedName("addressLine1") val village: String?,
    val addressLine2: String?,
    @SerializedName("city") val areaCouncil: String,
    val country: String,
    @SerializedName("district") val island: String,
    val postalCode: String?,
    @SerializedName("state") val province: String
) : Parcelable
