package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.MedFrequencyEnum

object MedFrequencyConverter {
    internal fun getMedFreqValue(freq: Int): String {
        return when (freq <= 4) {
            true -> MedFrequencyEnum.fromInt(freq).value
            else -> "$freq dose per day"
        }
    }
}