package com.latticeonfhir.features.appointment.navigation

const val appointmentRoute = "appointment"

sealed class Screen(val route: String) {
    data object PatientLandingScreen : Screen("patient_landing")
    data object Appointments : Screen("$appointmentRoute/appointment_screen")
    data object ScheduleAppointments : Screen("$appointmentRoute/schedule_appointments")
}