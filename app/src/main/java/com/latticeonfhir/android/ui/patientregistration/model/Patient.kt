package com.latticeonfhir.android.ui.patientregistration.model

import com.latticeonfhir.android.base.baseclass.ParcelableClass

data class PatientRegister(
    var firstName: String? = "",
    var middleName: String? = "",
    var lastName: String? = "",
    var phoneNumber: String? = "",
    var email: String? = "",
    var dobDay: String? = "",
    var dobMonth: String? = "",
    var dobYear: String? = "",
    var years: String? = "",
    var months: String? = "",
    var days: String? = "",
    var gender: String? = "",
    var passportId: String? = "",
    var voterId: String? = "",
    var patientId: String? = "",
    var homePostalCode: String? = "",
    var homeState: String? = "",
    var homeAddressLine1: String? = "",
    var homeAddressLine2: String? = "",
    var homeCity: String? = "",
    var homeDistrict: String? = "",
    var workPostalCode: String? = "",
    var workState: String? = "",
    var workAddressLine1: String? = "",
    var workAddressLine2: String? = "",
    var workCity: String? = "",
    var workDistrict: String? = ""
): ParcelableClass()