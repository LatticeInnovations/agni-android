package com.latticeonfhir.android.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.navigation.Screen

@Composable
fun AppointmentsFab(
    navController: NavController,
    patient: PatientResponse,
    isFabSelected: Boolean,
    appointment: AppointmentResponse?,
    ifAlreadyWaiting: Boolean,
    queueFabClicked: (Boolean) -> Unit
) {
    AnimatedVisibility(visible = !isFabSelected) {
        FloatingActionButton(
            onClick = { queueFabClicked(false) },
            modifier = Modifier
                .testTag("ADD_APPOINTMENT_FAB")
                .padding(bottom = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = null,
                Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    AnimatedVisibility(visible = isFabSelected) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column {
                FloatingActionButton(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient",
                            patient
                        )
                        navController.navigate(Screen.ScheduleAppointments.route)
                        queueFabClicked(false)
                    },
                    modifier = Modifier.testTag("ADD_SCHEDULE_FAB")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.schedule_appointment),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.today_icon),
                            contentDescription = null,
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (!ifAlreadyWaiting) {
                    FloatingActionButton(
                        onClick = { queueFabClicked(true) },
                        modifier = Modifier.testTag("QUEUE_FAB")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (appointment != null) stringResource(id = R.string.patient_arrived)
                                else stringResource(id = R.string.add_to_queue),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.playlist_add_circle_icon),
                                contentDescription = null,
                                Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                FloatingActionButton(
                    onClick = { queueFabClicked(false) },
                    modifier = Modifier
                        .testTag("CLEAR_FAB")
                        .padding(bottom = 20.dp)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}