package com.latticeonfhir.android.utils.converters.responseconverter.medication

import android.content.Context
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.MedFrequencyEnum
import org.hl7.fhir.r4.model.Medication

object MedicationInfoConverter {
    internal fun getMedInfo(
        frequency: Int,
        medUnit: String,
        timing: String?,
        note: String?,
        qtyPerDose: Int,
        duration: Int,
        context: Context
    ): String {
        return context.getString(
            R.string.med_info,
            qtyPerDose,
            medUnit,
            getMedFreqValue(frequency, context),
            if (timing?.isNotEmpty() == true) context.getString(R.string.timing, timing) else "",
            duration,
            frequency * qtyPerDose * duration,
            if (note?.isNotEmpty() == true) context.getString(R.string.notes, note) else ""
        )
    }

    private fun getMedFreqValue(freq: Int, context: Context): String {
        return when (freq <= 4) {
            true -> MedFrequencyEnum.fromInt(freq).value
            else -> context.getString(R.string.freq_dose_per_day, freq)
        }
    }

    fun getActiveIngredient(medication: Medication): String {
        return if( medication.ingredient.size == 1) {
            medication.ingredientFirstRep.itemCodeableConcept.codingFirstRep.display
        } else {
            var ai = ""
            medication.ingredient.forEach {  ing ->
                ai += if (ai.isBlank()) ing.itemCodeableConcept.codingFirstRep.display else "+${ing.itemCodeableConcept.codingFirstRep.display}"
            }
            ai
        }
    }
}