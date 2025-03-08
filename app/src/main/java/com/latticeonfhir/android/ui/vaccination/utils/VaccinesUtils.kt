package com.latticeonfhir.android.ui.vaccination.utils

import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import java.util.Calendar
import java.util.Date

object VaccinesUtils {
    private fun addWeeksToDate(date: Date, weeks: Int): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.WEEK_OF_YEAR, weeks)
        }
        return calendar.time
    }

    private fun addMonthsToDate(date: Date, months: Int): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.MONTH, months)
        }
        return calendar.time
    }

    private fun addYearsToDate(date: Date, years: Int): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.YEAR, years)
        }
        return calendar.time
    }

    fun List<ImmunizationRecommendation>.categorizeVaccines(dob: Date): Map<String, List<ImmunizationRecommendation>> {
        val categorizedVaccines = mutableMapOf<String, MutableList<ImmunizationRecommendation>>()

        val intervals = listOf(
            "At Birth" to dob,
            "6 Weeks" to addWeeksToDate(dob, 6),
            "10 Weeks" to addWeeksToDate(dob, 10),
            "14 Weeks" to addWeeksToDate(dob, 14),
            "6 Months" to addWeeksToDate(dob, 26),
            "7 Months" to addMonthsToDate(dob, 7),
            "9 Months" to addMonthsToDate(dob, 9),
            "12 Months" to addMonthsToDate(dob, 12),
            "15 Months" to addMonthsToDate(dob, 15),
            "16 Months" to addMonthsToDate(dob, 16),
            "18 Months" to addMonthsToDate(dob, 18),
            "4 Years" to addYearsToDate(dob, 4),
            "10 Years" to addYearsToDate(dob, 10)
        )

        for ((label, _) in intervals) {
            categorizedVaccines[label] = mutableListOf()
        }

        for (vaccine in this) {
            for ((label, date) in intervals) {
                if (date == vaccine.vaccineStartDate) {
                    categorizedVaccines[label]?.add(vaccine)
                }
            }
        }
        return categorizedVaccines
    }

    fun Int.getNumberWithOrdinalIndicator(): String {
        return "${this}${when(this) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }}"
    }
}