package com.heartcare.agni.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class PatientLastUpdatedResponse(
    val uuid: String,
    val timestamp: Date
) : Parcelable