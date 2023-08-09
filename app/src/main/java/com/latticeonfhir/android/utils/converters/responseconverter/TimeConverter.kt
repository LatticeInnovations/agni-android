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

    internal fun Date.toPrescriptionDate(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toSlotDate(): String {
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toAppointmentDate(): String {
        val formatter = SimpleDateFormat("dd MMM, yyyy · hh:mm a", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toAppointmentTime(): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toSlotStartTime(): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(this).toInt()
        val calendar = Calendar.getInstance()
        calendar.time = this
        if (minutes < 30) calendar.set(Calendar.MINUTE, 0)
        else calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return formatter.format(calendar.time)
    }

    internal fun Date.toYear(): String {
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toMonth(): String {
        val formatter = SimpleDateFormat("MMM", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.toWeekDay(): String {
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        return formatter.format(this)
    }

    internal fun Date.tomorrow(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }

    internal fun Date.yesterday(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.set(Calendar.DAY_OF_YEAR, calendar[Calendar.DAY_OF_YEAR]-1)
        return calendar.time
    }

    internal fun String.toCurrentTimeInMillis(date: Date): Long {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = inputFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.set(
            Calendar.HOUR_OF_DAY,
            SimpleDateFormat("HH", Locale.getDefault()).format(currentTime!!).toInt()
        )
        calendar.set(
            Calendar.MINUTE,
            SimpleDateFormat("mm", Locale.getDefault()).format(currentTime).toInt()
        )
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    internal fun String.to30MinutesAfter(date: Date): Long {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = inputFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.set(
            Calendar.HOUR_OF_DAY,
            SimpleDateFormat("HH", Locale.getDefault()).format(currentTime!!).toInt()
        )
        calendar.set(
            Calendar.MINUTE,
            SimpleDateFormat("mm", Locale.getDefault()).format(currentTime).toInt()
        )
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MINUTE, 30)
        return calendar.timeInMillis
    }

    internal fun String.to5MinutesAfter(date: Date): Long {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = inputFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.set(
            Calendar.HOUR_OF_DAY,
            SimpleDateFormat("HH", Locale.getDefault()).format(currentTime!!).toInt()
        )
        calendar.set(
            Calendar.MINUTE,
            SimpleDateFormat("mm", Locale.getDefault()).format(currentTime).toInt()
        )
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MINUTE, 5)
        return calendar.timeInMillis
    }

    internal fun Date.toWeekList(): List<Date> {
        val weekList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        var i = 0
        while (i < 8) {
            weekList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            i += 1
        }
        return weekList
    }

    internal fun Date.to14DaysWeek(): List<Date> {
        val weekList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        var i = 0
        while (i < 16) {
            weekList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            i += 1
        }
        return weekList
    }

    internal fun Date.toOneYearFuture(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.YEAR, 1)
        return calendar.time
    }

    internal fun Long.toOneYearPast(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        calendar.add(Calendar.YEAR, -1)
        return calendar.time
    }

    internal fun Date.toTodayStartDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    internal fun Date.toEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
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

    internal fun Date.calculateMinutesToOneThirty(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val endTimeMillis = calendar.timeInMillis
        return ((endTimeMillis - time) / (1000 * 60))
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
            formatter.format(calendar.time).replace("GMT", "")
        }
    }

    internal fun String.toTimeInMilli(): Long {
        val myDate = this
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(myDate)
        return date?.time ?: 0L
    }
}