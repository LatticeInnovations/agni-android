package com.latticeonfhir.android.utils.converters.responseconverter

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    internal fun String.toPatientDate(): String {
        val inputFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(this)
        val outputDate = outputFormat.format(date!!)
        return outputDate
    }
    internal fun String.toPatientPreviewDate(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val date = inputFormat.parse(this)
        val outputDate = outputFormat.format(date!!)
        return outputDate
    }

    internal fun ageToPatientDate(years: Int, months: Int, days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -years)
        calendar.add(Calendar.MONTH, -months)
        calendar.add(Calendar.DAY_OF_MONTH, -days)
        val dob = calendar.time
        val formatter = SimpleDateFormat("dd-MMMM-yyyy", Locale.US)
        return formatter.format(dob)
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

    internal fun Date.toPrescriptionDate() : String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(this)
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

    internal fun Long.toTimeStampDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time).replace("GMT","")
        }
    }

    internal fun String.toTimeInMilli(): Long {
        val myDate = this
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(myDate)
        return date?.time ?: 0L
    }
}