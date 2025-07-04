package com.heartcare.agni.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PatientIdentifier(
    val identifierType: String,
    val identifierNumber: String,
    val code: String?
) : Parcelable
