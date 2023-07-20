package com.latticeonfhir.android.ui.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class AppointmentsScreenViewModel : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("Upcoming", "Completed")

    var isFabSelected by mutableStateOf(false)

    var showCancelAppointmentDialog by mutableStateOf(false)
    var selectedAppointment by mutableStateOf("")

//    var upcomingAppointmentsList by mutableStateOf(
//        listOf<String>()
//    )

    var upcomingAppointmentsList by mutableStateOf(
        listOf(
            "12 Jun, 2023 · 09:00 AM",
            "14 Jun, 2023 · 10:00 AM"
        )
    )

//    var completedAppointmentsList by mutableStateOf(listOf<String>())

    var completedAppointmentsList by mutableStateOf(
        listOf(
            "1 Jun, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM",
            "22 May, 2023  ·  11:00 AM"
        )
    )
}