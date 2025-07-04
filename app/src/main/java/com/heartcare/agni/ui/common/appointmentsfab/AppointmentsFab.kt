package com.heartcare.agni.ui.common.appointmentsfab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.utils.constants.NavControllerConstants
import com.heartcare.agni.utils.constants.NavControllerConstants.ADD_TO_QUEUE
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT_ARRIVED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppointmentsFab(
    modifier: Modifier,
    navController: NavController,
    patient: PatientResponse,
    isFabSelected: Boolean,
    appointmentsFabViewModel: AppointmentsFabViewModel = hiltViewModel(),
    showDialog: (Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        appointmentsFabViewModel.initialize(patient.id)
    }
    Box(
        modifier =
        if (!isFabSelected) Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        else Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null
            ) {
                showDialog(false)
            },
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(visible = !isFabSelected) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = {
                        Snackbar(
                            content = {
                                Text(
                                    text = it.visuals.message
                                )
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .wrapContentHeight(Alignment.Bottom)
                )
                AddFAB(modifier, showDialog)
            }
        }
        AnimatedVisibility(visible = isFabSelected) {
            Column(
                modifier = modifier
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column {
                        ScheduleAppointmentFAB(navController, patient, showDialog)
                        Spacer(modifier = Modifier.height(20.dp))
                        if (!appointmentsFabViewModel.ifAlreadyWaiting) {
                            AddToQueueFAB(
                                navController,
                                patient,
                                appointmentsFabViewModel,
                                showDialog
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddToQueueFAB(
    navController: NavController,
    patient: PatientResponse,
    appointmentsFabViewModel: AppointmentsFabViewModel = hiltViewModel(),
    showDialog: (Boolean) -> Unit
) {
    FloatingActionButton(
        onClick = {
            //showDialog(true)
            if (appointmentsFabViewModel.appointment != null) {
                // change status of patient to arrived and navigate to queue screen
                appointmentsFabViewModel.updateStatusToArrived(
                    patient,
                    appointmentsFabViewModel.appointment!!
                ) {
                    CoroutineScope(Dispatchers.Main).launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT_ARRIVED,
                            true
                        )
                        navController.navigate(Screen.LandingScreen.route)
                    }
                }
            } else {
                // add patient to queue and navigate to queue screen
                if (appointmentsFabViewModel.ifAllSlotsBooked) {
                    showDialog(true)
                } else {
                    appointmentsFabViewModel.addPatientToQueue(patient) {
                        CoroutineScope(Dispatchers.Main).launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                ADD_TO_QUEUE,
                                true
                            )
                            navController.navigate(Screen.LandingScreen.route)
                        }
                    }
                }
            }

        },
        modifier = Modifier.testTag("QUEUE_FAB")
    ) {
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

@Composable
private fun ScheduleAppointmentFAB(
    navController: NavController,
    patient: PatientResponse,
    showDialog: (Boolean) -> Unit
) {
    FloatingActionButton(
        onClick = {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "patient",
                patient
            )
            navController.currentBackStackEntry?.savedStateHandle?.set(
                NavControllerConstants.IF_RESCHEDULING,
                false
            )
            navController.navigate(Screen.ScheduleAppointments.route)
            showDialog(false)
        },
        modifier = Modifier.testTag("ADD_SCHEDULE_FAB"),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp),
        contentColor = MaterialTheme.colorScheme.primary
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
}

@Composable
fun AddFAB(
    modifier: Modifier,
    showDialog: (Boolean) -> Unit
) {
    FloatingActionButton(
        onClick = { showDialog(false) },
        modifier = modifier
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
