package com.latticeonfhir.android.ui.main.patientregistration.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PatientRegister(
    var firstName: String? = "",
    var middleName: String? = "",
    var lastName: String? = "",
    var phoneNumber: String? = "",
    var email: String? = "",
    var dob: String? = "",
    var years: String? = "",
    var months: String? = "",
    var days: String? = "",
    var gender: String? = "",
    var passportId: String? = "",
    var voterId: String? = "",
    var patientId: String? = "",
    var homePostalCode: String? = "",
    var homeState: String? = "",
    var homeArea: String? = "",
    var homeTown: String? = "",
    var homeCity: String? = "",
    var workPostalCode: String? = "",
    var workState: String? = "",
    var workArea: String? = "",
    var workTown: String? = "",
    var workCity: String? = ""
): Parcelable