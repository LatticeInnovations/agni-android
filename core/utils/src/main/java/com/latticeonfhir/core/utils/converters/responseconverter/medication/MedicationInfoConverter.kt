package com.latticeonfhir.core.utils.converters.responseconverter.medication

import android.content.Context
import com.latticeonfhir.core.model.enums.MedFrequencyEnum
import com.latticeonfhir.core.utils.R

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
        return context.getString(
            R.string.med_info,
            qtyPerDose,
            medUnit,
            getMedFreqValue(
                frequency,
                context
            ),
            if (timing?.isNotEmpty() == true) context.getString(R.string.timing, timing) else "",
            duration,
            qtyPrescribed,
            if (note?.isNotEmpty() == true) context.getString(R.string.notes, note) else ""
        )
    }

    private fun getMedFreqValue(freq: Int, context: Context): String {
        return when (freq <= 4) {
            true -> MedFrequencyEnum.fromInt(freq).value
            else -> context.getString(R.string.freq_dose_per_day, freq)
        }
    }
}