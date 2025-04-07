package com.latticeonfhir.android.vaccination.utils

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

    internal fun numberOfWeeksToLabel(weeks: Int): String {
        return when(weeks) {
            0 -> "At Birth"
            6 -> "6 Weeks"
            10 -> "10 Weeks"
            14 -> "14 Weeks"
            26 -> "6 Months"
            30 -> "7 Months"
            39 -> "9 Months"
            52 -> "12 Months"
            56 -> "13 Months"
            69 -> "16 Months"
            78 -> "18 Months"
            104 -> "24 Months"
            208 -> "4 Years"
            313 -> "6 Years"
            469 -> "9 Years"
            730 -> "14 Years"
            782 -> "15 Years"
            938 -> "18 Years"
            else -> ""
        }
    }

    internal fun labelToNumberOfWeeks(label: String): Int? {
        return when (label) {
            "At Birth" -> 0
            "6 Weeks" -> 6
            "10 Weeks" -> 10
            "14 Weeks" -> 14
            "6 Months" -> 26
            "7 Months" -> 30
            "9 Months" -> 39
            "12 Months" -> 52
            "13 Months" -> 56
            "16 Months" -> 69
            "18 Months" -> 78
            "24 Months" -> 104
            "4 Years" -> 208
            "6 Years" -> 313
            "9 Years" -> 469
            "14 Years" -> 730
            "15 Years" -> 782
            "18 Years" -> 938
            else -> null
        }
    }

    private fun getDateIntervalList(dob: Date): List<Pair<String, Date>>{
        return listOf(
            "At Birth" to dob,
            "6 Weeks" to addWeeksToDate(dob, 6),
            "10 Weeks" to addWeeksToDate(dob, 10),
            "14 Weeks" to addWeeksToDate(dob, 14),
            "6 Months" to addWeeksToDate(dob, 26),
            "7 Months" to addWeeksToDate(dob, 30),
            "9 Months" to addWeeksToDate(dob, 39),
            "12 Months" to addWeeksToDate(dob, 52),
            "13 Months" to addWeeksToDate(dob, 56),
            "16 Months" to addWeeksToDate(dob, 69),
            "18 Months" to addWeeksToDate(dob, 78),
            "24 Months" to addWeeksToDate(dob, 104),
            "4 Years" to addWeeksToDate(dob, 208),
            "6 Years" to addWeeksToDate(dob, 313),
            "9 Years" to addWeeksToDate(dob, 469),
            "14 Years" to addWeeksToDate(dob, 730),
            "15 Years" to addWeeksToDate(dob, 782),
            "18 Years" to addWeeksToDate(dob, 938)
        )
    }

    fun List<ImmunizationRecommendation>.categorizeVaccines(dob: Date): Map<String, List<ImmunizationRecommendation>> {
        val categorizedVaccines = mutableMapOf<String, MutableList<ImmunizationRecommendation>>()

        val intervals = getDateIntervalList(dob)

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
        return categorizedVaccines.filter { it.value.isNotEmpty() }
    }

    fun Int.getNumberWithOrdinalIndicator(): String {
        return "${this}${when(this) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }}"
    }

    fun Int.formatBytes(): String {
        val kb = this / 1024
        return if (kb < 1024) {
            "$kb kb"
        } else {
            val mb = kb / 1024
            "$mb mb"
        }
    }
}