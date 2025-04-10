package com.latticeonfhir.android.ui.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.core.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.utils.constants.NavControllerConstants.APPOINTMENT_SELECTED
import com.latticeonfhir.core.utils.constants.NavControllerConstants.IF_RESCHEDULING
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toAppointmentDate

@Composable
fun UpcomingAppointments(navController: NavController, viewModel: AppointmentsScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (viewModel.upcomingAppointmentsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.no_upcoming_appointments))
            }
        } else {
            viewModel.upcomingAppointmentsList.forEach { appointmentResponseLocal ->
                UpcomingAppointmentCard(navController, appointmentResponseLocal, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UpcomingAppointmentCard(
    navController: NavController,
    appointmentResponseLocal: AppointmentResponseLocal,
    viewModel: AppointmentsScreenViewModel
) {
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.testTag("UPCOMING_APPOINTMENT_CARD")
    ) {
        Text(
            text = appointmentResponseLocal.slot.start.toAppointmentDate(),
            modifier = Modifier
                .padding(
                    vertical = 32.dp,
                    horizontal = 16.dp
                )
                .testTag("APPOINTMENT_DATE_AND_TIME")
        )
        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.surface
            ) {
                TextButton(
                    modifier = Modifier.testTag("APPOINTMENT_CANCEL_BTN"),
                    onClick = {
                        viewModel.selectedAppointment = appointmentResponseLocal
                        viewModel.showCancelAppointmentDialog = true
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                TextButton(
                    modifier = Modifier.testTag("APPOINTMENT_RESCHEDULE_BTN"),
                    onClick = {
                        viewModel.selectedAppointment = appointmentResponseLocal
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            APPOINTMENT_SELECTED,
                            viewModel.selectedAppointment
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            IF_RESCHEDULING,
                            true
                        )
                        navController.navigate(Screen.ScheduleAppointments.route)
                    }) {
                    Text(text = stringResource(id = R.string.reschedule))
                }
            }
        }
    }
}