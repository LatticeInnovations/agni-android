package com.heartcare.agni.ui.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.heartcare.agni.base.viewmodel.BaseViewModel

class PatientRegistrationStepThreeViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    val other = "Other"
    val maxLength = 50
    val postalCodeLength = 10

    var province by mutableStateOf("")
    var provinceList = listOf("One", "Two", "Three", "Four")
    var isProvinceOtherSelected by mutableStateOf(false)
    var otherProvince by mutableStateOf("")
    var otherProvinceError by mutableStateOf(false)

    var areaCouncil by mutableStateOf("")
    var areaCouncilList = listOf("One", "Two", "Three", "Four")
    var isAreaCouncilOtherSelected by mutableStateOf(false)
    var otherAreaCouncil by mutableStateOf("")
    var otherAreaCouncilError by mutableStateOf(false)

    var island by mutableStateOf("")
    var islandList = listOf("One", "Two", "Three", "Four")
    var isIslandOtherSelected by mutableStateOf(false)
    var otherIsland by mutableStateOf("")
    var otherIslandError by mutableStateOf(false)

    var village by mutableStateOf("")
    var villageList = listOf("One", "Two", "Three", "Four")
    var isVillageOtherSelected by mutableStateOf(false)
    var otherVillage by mutableStateOf("")
    var otherVillageError by mutableStateOf(false)

    var postalCode by mutableStateOf("")

    var homeAddress by mutableStateOf(Address())

    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    fun addressInfoValidation(): Boolean {
        // Province is mandatory
        if (province.isBlank()) return false

        // Province = "Other": require lower others
        if (province == other) {
            return otherProvince.isNotBlank()
                    && otherAreaCouncil.isNotBlank()
                    && otherIsland.isNotBlank()
                    && otherVillage.isNotBlank()
        }

        // areaCouncil
        if (areaCouncil.isBlank()) return false

        // areaCouncil = "Other": require lower others
        if (areaCouncil == other) {
            return otherAreaCouncil.isNotBlank()
                    && otherIsland.isNotBlank()
                    && otherVillage.isNotBlank()
        }

        // island
        if (island.isBlank()) return false

        // island = "Other": require lower others
        if (island == other) {
            return otherIsland.isNotBlank()
                    && otherVillage.isNotBlank()
        }

        // village = "Other": only otherVillage required
        if (village == other) {
            return otherVillage.isNotBlank()
        }

        return true
    }
}

class Address {
    var pincode by mutableStateOf("")
    var state by mutableStateOf("")
    var addressLine1 by mutableStateOf("")
    var addressLine2 by mutableStateOf("")
    var city by mutableStateOf("")
    var district by mutableStateOf("")
    var isPostalCodeValid by mutableStateOf(false)
    var isAddressLine1Valid by mutableStateOf(false)
    var isCityValid by mutableStateOf(false)
    var isStateValid by mutableStateOf(false)
}