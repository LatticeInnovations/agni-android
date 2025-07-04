package com.heartcare.agni.data.server.model.scheduleandappointment

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Slot(
    val start: Date,
    val end: Date
) : Parcelable
