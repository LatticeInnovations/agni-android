package com.heartcare.agni.ui.patienteditscreen.address

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPatientAddressViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository,
    private val levelRepository: LevelRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val other = LevelResponse(
        fhirId = "others",
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

    var areaCouncil: LevelResponse? by mutableStateOf(null)
    var areaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())

    var island: LevelResponse? by mutableStateOf(null)
    var islandList: List<LevelResponse> by mutableStateOf(emptyList())

    var village: LevelResponse? by mutableStateOf(null)
    var villageList: List<LevelResponse> by mutableStateOf(emptyList())
    var isVillageOtherSelected by mutableStateOf(false)
    var otherVillage by mutableStateOf("")
    var otherVillageError by mutableStateOf(false)

    var postalCode by mutableStateOf("")

    var provinceTemp: LevelResponse? by mutableStateOf(null)
    var areaCouncilTemp: LevelResponse? by mutableStateOf(null)
    var islandTemp: LevelResponse? by mutableStateOf(null)
    var villageTemp: LevelResponse? by mutableStateOf(null)
    var otherVillageTemp by mutableStateOf("")
    var postalCodeTemp by mutableStateOf("")

    fun getLists() {
        viewModelScope.launch(Dispatchers.IO) {
            provinceList =
                levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType)
            getAreaCouncilList()
            getIslandList()
            getVillageList()
        }
    }

    fun getAreaCouncilList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            areaCouncilList = levelRepository.getLevels(
                levelType = LevelsEnum.AREA_COUNCIL.levelType,
                precedingId = province!!.fhirId
            )
        }
    }

    fun getIslandList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            islandList = levelRepository.getLevels(
                levelType = LevelsEnum.ISLAND.levelType,
                precedingId = areaCouncil!!.fhirId
            )
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

    suspend fun getLevelByFhirId(fhirId: String): LevelResponse {
        return levelRepository.getLevelByFhirId(fhirId)
    }

    fun addressInfoValidation(): Boolean {
        if (province == null || areaCouncil == null || island == null) return false

        if (village == other) {
            return otherVillage.isNotBlank()
        }

        return true
    }

    fun checkIsEdit(): Boolean {
        return postalCode != postalCodeTemp ||
                province != provinceTemp ||
                areaCouncil != areaCouncilTemp ||
                island != islandTemp ||
                village != villageTemp ||
                otherVillage != otherVillageTemp
    }


    fun revertChanges(): Boolean {
        postalCode = postalCodeTemp
        province = provinceTemp
        areaCouncil = areaCouncilTemp
        island = islandTemp
        village = villageTemp
        otherVillage = otherVillageTemp
        otherVillageError = false
        isVillageOtherSelected = otherVillage.isNotBlank()
        return true
    }

    fun updateAddressInfo(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (checkIsEdit() && response > 0) {
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        patientResponse = patientResponse
                    )
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }
}

