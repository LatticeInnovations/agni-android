package com.latticeonfhir.android.ui.appointments.reschedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekList
import java.util.Date

class RescheduleAppointmentViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var appointment by mutableStateOf("")
    var showDatePicker by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date())
    var weekList by mutableStateOf(selectedDate.toWeekList())
    var selectedSlot by mutableStateOf("")
    var morningSlots by mutableStateOf(listOf(
        "3",
        "3",
        "3",
        "3",
        "0",
        "3"
    ))
    var afternoonSlots by mutableStateOf(listOf(
        "3",
        "3",
        "3",
        "3",
        "3",
        "0",
        "0",
        "3",
        "3"
    ))
    var eveningSlots by mutableStateOf(listOf(
        "3",
        "3"
    ))
}