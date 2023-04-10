package com.latticeonfhir.android.utils.converters.responseconverter

import java.text.SimpleDateFormat
import java.util.*

object TimeConverter {

    internal fun Long.toAge(): Int {
        val present = Calendar.getInstance()
        val personBirthDate = Calendar.getInstance()
        personBirthDate.timeInMillis = this

        val yearDiff = present[Calendar.YEAR] - personBirthDate[Calendar.YEAR]
        val presentDay = if (present.isLeapYear() && present[Calendar.MONTH] > Calendar.FEBRUARY)
            present[Calendar.DAY_OF_YEAR] - 1 else present[Calendar.DAY_OF_YEAR]
        val birthDay =
            if (personBirthDate.isLeapYear() && personBirthDate[Calendar.MONTH] > Calendar.FEBRUARY)
                personBirthDate[Calendar.DAY_OF_YEAR] - 1 else personBirthDate[Calendar.DAY_OF_YEAR]

        return (when {
            presentDay - birthDay > 0 -> {
                yearDiff
            }
            presentDay - birthDay < 0 -> {
                yearDiff - 1
            }
            else -> {
                yearDiff
            }
        }).coerceAtLeast(0)
    }

    internal fun Long.toAge(from: Long): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        today.timeInMillis = from
        dob.timeInMillis = this

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    internal fun Calendar.isLeapYear(): Boolean {
        return (this[Calendar.YEAR] % 400 == 0 ||
                (this[Calendar.YEAR] % 4 == 0 && this[Calendar.YEAR] % 100 != 0))
    }

    internal fun Long.toDate(): String {
        return if (this != 0.toLong()) {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            ""
        }
    }

    internal fun Long.toPatientDate(): String {
        return if (this != 0.toLong()) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            ""
        }
    }

    internal fun String.toTimeInMilli(): Long {
        val myDate = this
        val sdf = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        val date = sdf.parse(myDate)
        return date?.time ?: 0L
    }
}