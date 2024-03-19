package com.latticeonfhir.android.ui.common.appointmentsfab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.constants.NavControllerConstants.ADD_TO_QUEUE
import com.latticeonfhir.android.utils.constants.NavControllerConstants.IF_RESCHEDULING
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_ARRIVED
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient

@Composable
fun AppointmentsFab(
    navController: NavController,
    patient: Patient,
    isFabSelected: Boolean,
    appointmentsFabViewModel: AppointmentsFabViewModel = hiltViewModel(),
    showDialog: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        appointmentsFabViewModel.initialize(patient.logicalId)
    }
    AnimatedVisibility(visible = !isFabSelected) {
        FloatingActionButton(
            onClick = { showDialog(false) },
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
                            PATIENT,
                            patient
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            IF_RESCHEDULING,
                            false
                        )
                        navController.navigate(Screen.ScheduleAppointments.route)
                        showDialog(false)
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
                if (!appointmentsFabViewModel.ifAlreadyWaiting) {
                    FloatingActionButton(
                        onClick = {
                            if (appointmentsFabViewModel.appointment != null) {
                                // change status of patient to arrived and navigate to queue screen
                                appointmentsFabViewModel.adding = true
                                appointmentsFabViewModel.updateStatusToArrived(
                                    appointmentsFabViewModel.appointment!!
                                ) {
                                    appointmentsFabViewModel.adding = false
                                    coroutineScope.launch {
                                        navController.popBackStack(
                                            Screen.PatientLandingScreen.route,
                                            false
                                        )
                                        navController.previousBackStackEntry?.savedStateHandle?.set(
                                            PATIENT_ARRIVED,
                                            true
                                        )
                                        navController.popBackStack()
                                    }
                                }
                            } else {
                                // add patient to queue and navigate to queue screen
                                if (appointmentsFabViewModel.ifAllSlotsBooked) {
                                    showDialog(true)
                                } else {
                                    appointmentsFabViewModel.adding = true
                                    appointmentsFabViewModel.addPatientToQueue(patient) {
                                        appointmentsFabViewModel.adding = false
                                        coroutineScope.launch {
                                            navController.popBackStack(
                                                Screen.PatientLandingScreen.route,
                                                false
                                            )
                                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                                ADD_TO_QUEUE,
                                                true
                                            )
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("QUEUE_FAB")
                    ) {
                        if (appointmentsFabViewModel.adding) {
                            Box(
                                modifier = Modifier
                                    .wrapContentHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(20.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (appointmentsFabViewModel.appointment != null) stringResource(
                                        id = R.string.patient_arrived
                                    )
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                FloatingActionButton(
                    onClick = { showDialog(false) },
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