package com.latticeonfhir.features.appointment.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.appointment.ui.AppointmentsScreen
import com.latticeonfhir.features.appointment.ui.schedule.ScheduleAppointments

fun NavGraphBuilder.appointmentNavGraph(
    navController: NavController
) {
    composable(Screen.Appointments.route) { AppointmentsScreen(navController = navController) }
    composable(Screen.ScheduleAppointments.route) { ScheduleAppointments(navController = navController) }
}