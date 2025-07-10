package com.heartcare.agni.ui.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationStepThreeViewModel @Inject constructor(
    private val levelRepository: LevelRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    val other = LevelResponse(
        fhirId = "0",
        code = "0",
        levelType = "others",
        name = "Others",
        population = null,
        precedingLevelId = null,
        secondaryName = null,
        status = "active"
    )
    val maxLength = 50
    val postalCodeLength = 10

    var province: LevelResponse? by mutableStateOf(null)
    var provinceList: List<LevelResponse> by mutableStateOf(emptyList())
    var isProvinceOtherSelected by mutableStateOf(false)
    var otherProvince by mutableStateOf("")
    var otherProvinceError by mutableStateOf(false)

    var areaCouncil: LevelResponse? by mutableStateOf(null)
    var areaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())
    var isAreaCouncilOtherSelected by mutableStateOf(false)
    var otherAreaCouncil by mutableStateOf("")
    var otherAreaCouncilError by mutableStateOf(false)

    var island: LevelResponse? by mutableStateOf(null)
    var islandList: List<LevelResponse> by mutableStateOf(emptyList())
    var isIslandOtherSelected by mutableStateOf(false)
    var otherIsland by mutableStateOf("")
    var otherIslandError by mutableStateOf(false)

    var village: LevelResponse? by mutableStateOf(null)
    var villageList: List<LevelResponse> by mutableStateOf(emptyList())
    var isVillageOtherSelected by mutableStateOf(false)
    var otherVillage by mutableStateOf("")
    var otherVillageError by mutableStateOf(false)

    var postalCode by mutableStateOf("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            provinceList = levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType) + listOf(other)
        }
    }

    fun getAreaCouncilList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            areaCouncilList = levelRepository.getLevels(
                levelType = LevelsEnum.AREA_COUNCIL.levelType,
                precedingId = province!!.fhirId
            ) + listOf(other)
        }
    }

    fun getIslandList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            islandList = levelRepository.getLevels(
                levelType = LevelsEnum.ISLAND.levelType,
                precedingId = areaCouncil!!.fhirId
            ) + listOf(other)
        }
    }

    fun getVillageList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            villageList = levelRepository.getLevels(
                levelType = LevelsEnum.VILLAGE.levelType,
                precedingId = island!!.fhirId
            ) + listOf(other)
        }
    }

    fun addressInfoValidation(): Boolean {
        // Province is mandatory
        if (province == null) return false

        // Province = "Other": require lower others
        if (province == other) {
            return otherProvince.isNotBlank()
                    && otherAreaCouncil.isNotBlank()
                    && otherIsland.isNotBlank()
                    && otherVillage.isNotBlank()
        }

        // areaCouncil
        if (areaCouncil == null) return false

        // areaCouncil = "Other": require lower others
        if (areaCouncil == other) {
            return otherAreaCouncil.isNotBlank()
                    && otherIsland.isNotBlank()
                    && otherVillage.isNotBlank()
        }

        // island
        if (island == null) return false

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