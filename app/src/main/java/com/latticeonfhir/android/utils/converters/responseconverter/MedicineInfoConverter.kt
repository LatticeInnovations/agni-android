package com.latticeonfhir.android.utils.converters.responseconverter

object MedicineInfoConverter {
    internal fun getMedInfo(
        frequency: Int,
        medUnit: String,
        timing: String?,
        note: String?,
        qtyPerDose: Int,
        duration: Int,
        qtyPrescribed: Int
    ): String {
        return "$qtyPerDose $medUnit ${
            MedFrequencyConverter.getMedFreqValue(frequency)
        }, $timing\n" +
                "Duration : $duration days , Qty : $qtyPrescribed" +
                if (note?.isNotEmpty() == true) "\nNotes : $note" else ""
    }
}