package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.MedFrequencyEnum

object MedFrequencyConverter {
    internal fun getMedFreqValue(freq: Int): String {
        return when(freq){
            1 -> MedFrequencyEnum.fromInt(1).value
            2 -> MedFrequencyEnum.fromInt(2).value
            3 -> MedFrequencyEnum.fromInt(3).value
            4 -> MedFrequencyEnum.fromInt(4).value
            else -> "$freq dose per day"
        }
    }
}