package com.latticeonfhir.android.utils.converters

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeConverter {

    private const val YYYY_MM_DD = "yyyy-MM-dd"
    private const val DD_MM_YYYY = "dd MMM yyyy"
    private const val HH_MM_A = "hh:mm a"

    fun Long.toAge(): Int {
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

    fun Long.toAge(from: Long): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        today.timeInMillis = from
        dob.timeInMillis = this

        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]

        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    fun String.toPatientDate(): String {
        val inputFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.US)
        val outputFormat = SimpleDateFormat(YYYY_MM_DD, Locale.US)
        val date = inputFormat.parse(this)
        return outputFormat.format(date!!)
    }

    fun String.toPatientPreviewDate(): String {
        val inputFormat = SimpleDateFormat(YYYY_MM_DD, Locale.US)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val date = inputFormat.parse(this)
        return outputFormat.format(date!!)
    }

    fun ageToPatientDate(years: Int, months: Int, days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -years)
        calendar.add(Calendar.MONTH, -months)
        calendar.add(Calendar.DAY_OF_MONTH, -days)
        val dob = calendar.time
        val formatter = SimpleDateFormat("dd-MMMM-yyyy", Locale.US)
        return formatter.format(dob)
    }

    private fun Calendar.isLeapYear(): Boolean {
        return (this[Calendar.YEAR] % 400 == 0 ||
                (this[Calendar.YEAR] % 4 == 0 && this[Calendar.YEAR] % 100 != 0))
    }

    fun Long.toDate(): String {
        return if (this != 0.toLong()) {
            val formatter = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            ""
        }
    }

    fun isDOBValid(day: Int, month: Int, year: Int): Boolean {
        if (year < 1900 || year > Date().toYear().toInt()) return false
        val maxDaysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> return false
        }
        if (day < 1 || day > maxDaysInMonth) {
            return false
        }
        // check for future
        val todayDate = Date()
        return when (true) {
            (year > todayDate.toYear().toInt()) -> false
            (year == todayDate.toYear().toInt() && month > todayDate.toMonth()
                .toMonthInteger()) -> false

            (year == todayDate.toYear().toInt() && month == todayDate.toMonth()
                .toMonthInteger() && day > todayDate.toDateInteger()) -> false

            else -> true
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun Date.toPrescriptionDate(): String {
        val formatter = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toSlotDate(): String {
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toAppointmentDate(): String {
        val formatter = SimpleDateFormat("dd MMM, yyyy · hh:mm a", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toAppointmentTime(): String {
        val formatter = SimpleDateFormat(HH_MM_A, Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toSlotStartTime(): String {
        val formatter = SimpleDateFormat(HH_MM_A, Locale.getDefault())
        val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(this).toInt()
        val calendar = Calendar.getInstance()
        calendar.time = this
        if (minutes < 30) calendar[Calendar.MINUTE] = 0
        else calendar[Calendar.MINUTE] = 30
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return formatter.format(calendar.time)
    }

    fun Date.toYear(): String {
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toMonth(): String {
        val formatter = SimpleDateFormat("MMM", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toMonthAndYear(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return formatter.format(this)
    }

    fun String.toMonthInteger(): Int {
        val inputFormatter = SimpleDateFormat("MMM", Locale.getDefault())
        val currentMonth = inputFormatter.parse(this)
        val outputFormatter = SimpleDateFormat("MM", Locale.getDefault())
        return outputFormatter.format(currentMonth!!).toInt()
    }

    private fun Date.toDateInteger(): Int {
        val formatter = SimpleDateFormat("dd", Locale.getDefault())
        return formatter.format(this).toInt()
    }

    fun Date.toWeekDay(): String {
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.tomorrow(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.time
    }

    fun Date.yesterday(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] - 1
        return calendar.time
    }

    private fun String.toCalenderTime(date: Date): Calendar {
        val inputFormat = SimpleDateFormat(HH_MM_A, Locale.getDefault())
        val currentTime = inputFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar[Calendar.HOUR_OF_DAY] =
            SimpleDateFormat("HH", Locale.getDefault()).format(currentTime!!).toInt()
        calendar[Calendar.MINUTE] =
            SimpleDateFormat("mm", Locale.getDefault()).format(currentTime).toInt()
        return calendar
    }

    fun String.toCurrentTimeInMillis(date: Date): Long {
        val calendar = this.toCalenderTime(date)
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    fun String.to30MinutesAfter(date: Date): Long {
        val calendar = this.toCalenderTime(date)
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 0
        calendar.add(Calendar.MINUTE, 29)
        return calendar.timeInMillis
    }

    fun String.to5MinutesAfter(date: Date): Long {
        val calendar = this.toCalenderTime(date)
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar.add(Calendar.MINUTE, 5)
        return calendar.timeInMillis
    }

    fun Date.toWeekList(): List<Date> {
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

    fun Date.to14DaysWeek(): List<Date> {
        val weekList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        var counter = 0
        while (counter < 15) {
            weekList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            counter += 1
        }
        return weekList
    }

    fun Date.toOneYearFuture(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar.add(Calendar.YEAR, 1)
        return calendar.time
    }

    fun Long.toOneYearPast(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        calendar.add(Calendar.YEAR, -1)
        return calendar.time
    }

    fun Date.toTodayStartDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    fun Date.toEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.time
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    fun Long.toPatientDate(): String {
        return if (this != 0.toLong()) {
            val formatter = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            formatter.format(calendar.time)
        } else {
            ""
        }
    }

    fun Date.calculateMinutesToOneThirty(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar[Calendar.HOUR_OF_DAY] = 1
        calendar[Calendar.MINUTE] = 30
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val endTimeMillis = calendar.timeInMillis
        return ((endTimeMillis - time) / (1000 * 60))
    }

    fun Long.toTimeStampDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return formatter.format(calendar.time)
    }

    fun String.toTimeInMilli(): Long {
        val myDate = this
        val sdf = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
        val date = sdf.parse(myDate)
        return date?.time ?: 0L
    }

    fun Date.toLastSyncTime(): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy, HH:mm", Locale.getDefault())
        return sdf.format(this)
    }

    fun Date.toDayFullMonthYear(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toPrescriptionNavDate(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy · HH:mm", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.toPrescriptionTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(this)
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(date: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return isSameDay(date, cal.time)
    }

    fun isToday(date: Date): Boolean {
        return isSameDay(date, Date())
    }

    fun lastWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    fun lastMonth(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    fun lastThreeMonth(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -3)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    fun lastYear(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    fun Date.toddMMMyyyy(): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return sdf.format(this)
    }


    fun Date.formatDateToDayMonth(): String {
        // Create a formatter to get the day and short month (e.g., "04 Mar")
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        return formatter.format(this)
    }
    fun String.convertStringToDate(): Date {
        val formatter = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
        return formatter.parse(this)?:Date()
    }
    fun String.convertDateFormat(): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat(YYYY_MM_DD, Locale.ENGLISH)
        val outputFormat = SimpleDateFormat(DD_MM_YYYY, Locale.ENGLISH)

        // Parse the input date string
        val date: Date? = inputFormat.parse(this)

        // Format the date into the desired output format
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "Invalid Date"
        }
    }
    fun Date.convertedDate(): String {
        val sdf = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
        return sdf.format(this)
    }

    fun String.toDate(format: String): Date {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.parse(this)!!
    }

    fun formatAgeInDaysWeeksMonthsYears(birthDate: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birth = sdf.parse(birthDate) ?: return "Invalid Date"
        val today = Date()

        val diff = today.time - birth.time
        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        val weeks = days / 7
        val remainingDays = days % 7
        val months = days / 30
        val completedWeeks = (days % 30) / 7
        val years = days / 365
        val completedMonths = (days % 365) / 30

        return when {
            days <= 14 -> "$days day"
            days in 15 until 105 -> "$weeks week $remainingDays day"
            days in 105 until 365 -> "$months month $completedWeeks week"
            else -> "$years year $completedMonths month"
        }
    }

    fun Date.toddMMYYYYString(): String {
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return outputFormat.format(this)
    }

    fun Date.toFileDateAndTimeName(): String {
        val outputFormat = SimpleDateFormat("dd-MM-yyyy | HH:mm", Locale.getDefault())
        return outputFormat.format(this)
    }

    fun daysBetween(date1: Date, date2: Date): Int {
        val diffInMillis = date2.time - date1.time
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    }

    fun Date.plusMinusDays(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DATE, days)
        return calendar.time
    }
}