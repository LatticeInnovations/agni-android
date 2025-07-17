package com.heartcare.agni.ui.historyandtests.priordx

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.heartcare.agni.data.local.enums.PriorDiagnosis

class AddPriorDxViewModel : ViewModel() {
    val maxCancerFieldLength = 200
    val maxOtherFieldLength = 200

    var selectedPriorDx by mutableStateOf(listOf<String>())
    var cancerField by mutableStateOf("")
    var isCancerFieldError by mutableStateOf(false)
    var otherField by mutableStateOf("")
    var isOtherFieldError by mutableStateOf(false)

    fun isValid(): Boolean {
        return when {
            PriorDiagnosis.OTHERS.display in selectedPriorDx && otherField.isBlank() -> false
            PriorDiagnosis.CANCER.display in selectedPriorDx && cancerField.isBlank() -> false
            else -> selectedPriorDx.isNotEmpty()
        }
    }
}