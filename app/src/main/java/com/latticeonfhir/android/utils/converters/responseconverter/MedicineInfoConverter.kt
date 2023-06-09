package com.latticeonfhir.android.utils.converters.responseconverter

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
            MedFrequencyConverter.getMedFreqValue(frequency)
        }, $timing\n" +
                "Duration : $duration days , Qty : $qtyPerDose" +
                if (note?.isNotEmpty() == true) "\nNotes : $note" else ""
    }
}