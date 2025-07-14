package com.heartcare.agni.ui.searchpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.LastVisit.Companion.getLastVisitList
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPatientViewModel @Inject constructor(
    private val levelRepository: LevelRepository
) : BaseViewModel() {
    val onlyNumbers = Regex("^\\d+\$")
    var isLaunched by mutableStateOf(false)

    val maxHeartcareIdLength = 8
    val maxHospitalIdLength = 6
    val maxNationalIdLength = 6

    val select = LevelResponse(
        fhirId = "",
        code = "",
        levelType = "",
        name = "Select",
        population = null,
        precedingLevelId = null,
        secondaryName = null,
        status = ""
    )

    var fromHouseholdMember by mutableStateOf(false)
    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var patientName by mutableStateOf("")
    var patientId by mutableStateOf("")
    var gender by mutableStateOf("")
    var minAge by mutableStateOf("0")
    var maxAge by mutableStateOf("100")
    var visitSelected by mutableStateOf(getLastVisitList()[0])

    var riskCategory by mutableStateOf("")

    var range by mutableStateOf(minAge.toFloat()..maxAge.toFloat())

    var heartcareId by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var hospitalId by mutableStateOf("")

    var province by mutableStateOf(select)
    var provinceList: List<LevelResponse> by mutableStateOf(emptyList())

    var areaCouncil by mutableStateOf(select)
    var areaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())

    var address = Address()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            provinceList = listOf(select) + levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType)
            getAreaCouncilList()
        }
    }

    fun getAreaCouncilList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            areaCouncilList = listOf(select) + levelRepository.getLevels(
                levelType = LevelsEnum.AREA_COUNCIL.levelType,
                precedingId = province.fhirId.ifBlank { null }
            )
        }
    }

    fun updateRange(minAge: String, maxAge: String) {
        val min: String = minAge.ifEmpty { "0" }
        val max: String = maxAge.ifEmpty { "0" }
        range = min.toFloat()..max.toFloat()
    }
}