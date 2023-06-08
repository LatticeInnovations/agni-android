package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.enums.MedFrequencyEnum

object MedicineInfoConverter {
    internal fun getMedInfo(
        frequency: Int,
        medUnit: String,
        timing: String?,
        note: String?,
        qtyPerDose: Int,
        duration: Int
    ): String {
        return "$frequency $medUnit ${
            MedFrequencyEnum.fromInt(
                frequency
            )
        }, $timing\n" +
                "Duration : $duration days , Qty : $qtyPerDose" +
                if (note?.isNotEmpty() == true) "\nNotes : $note" else ""
    }
}