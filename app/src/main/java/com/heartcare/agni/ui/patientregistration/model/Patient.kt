package com.heartcare.agni.ui.patientregistration.model

import android.os.Parcelable
import com.heartcare.agni.data.server.model.levels.LevelResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class PatientRegister(
    var firstName: String? = "",
    var lastName: String? = "",
    var phoneNumber: String? = "",
    var dobAgeSelector: String? = "dob",
    var dobDay: String? = "",
    var dobMonth: String? = "",
    var dobYear: String? = "",
    var years: String? = "",
    var months: String? = "",
    var days: String? = "",
    var gender: String? = "",
    var isPersonDeceased: Int? = 0,
    var personDeceasedReason: String? = "",
    var motherName: String? = "",
    var fatherName: String? = "",
    var spouseName: String? = "",

    var hospitalId: String? = "",
    var nationalId: String? = "",
    var nationalIdUse: String? = "",

    var province: LevelResponse? = null,
    var areaCouncil: LevelResponse? = null,
    var island: LevelResponse? = null,
    var village: LevelResponse? = null,
    var postalCode: String? = "",
    var otherProvince: String? ="",
    var otherAreaCouncil: String? ="",
    var otherIsland: String? ="",
    var otherVillage: String? ="",
) : Parcelable