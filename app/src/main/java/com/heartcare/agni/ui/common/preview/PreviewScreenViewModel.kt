package com.heartcare.agni.ui.common.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewScreenViewModel @Inject constructor(
    private val levelRepository: LevelRepository
): BaseViewModel() {
    var provinceName by mutableStateOf("")
    var areaCouncilName by mutableStateOf("")
    var islandName by mutableStateOf("")
    var villageName by mutableStateOf("")

    suspend fun getLevelNames(fhirId: String): String {
        return levelRepository.getLevelNameFromFhirId(fhirId)
    }
}