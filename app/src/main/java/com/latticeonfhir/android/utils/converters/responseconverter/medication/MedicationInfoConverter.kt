package com.latticeonfhir.android.utils.converters.responseconverter.medication

import android.content.Context
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.MedFrequencyEnum

object MedicationInfoConverter {
    internal fun getMedInfo(
        frequency: Int,
        medUnit: String,
        timing: String?,
        note: String?,
        qtyPerDose: Int,
        duration: Int,
        qtyPrescribed: Int,
        context: Context
    ): String {
        return "$qtyPerDose $medUnit ${
            getMedFreqValue(frequency, context)
        }, $timing\n" +
                "Duration : $duration days , Qty : $qtyPrescribed" +
                if (note?.isNotEmpty() == true) "\nNotes : $note" else ""
    }

    private fun getMedFreqValue(freq: Int, context: Context): String {
        return when (freq <= 4) {
            true -> MedFrequencyEnum.fromInt(freq).value
            else -> context.getString(R.string.freq_dose_per_day, freq)
        }
    }
}