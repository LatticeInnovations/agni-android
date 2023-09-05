package com.latticeonfhir.android.utils.converters.responseconverter

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object MonthsList {
    internal fun getMonthsList(dobDay: String): SnapshotStateList<String> {
        return if (dobDay.toInt() > 30) mutableStateListOf(
                "January",
                "March",
                "May",
                "July",
                "August",
                "October",
                "December"
            )
            else if (dobDay.toInt() > 29) mutableStateListOf(
                "January", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            else mutableStateListOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
    }
}