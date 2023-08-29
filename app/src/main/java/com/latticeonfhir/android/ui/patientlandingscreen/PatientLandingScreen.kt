package com.latticeonfhir.android.ui.patientlandingscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.AppointmentsFab
import com.latticeonfhir.android.utils.constants.NavControllerConstants.ADD_TO_QUEUE
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_ARRIVED
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLandingScreen(
    navController: NavController,
    viewModel: PatientLandingScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
            viewModel.patient?.fhirId?.let { patientFhirId ->
                viewModel.downloadPrescriptions(
                    patientFhirId
                )
            }
        }
        viewModel.patient = viewModel.getPatientData(viewModel.patient!!.id)

        viewModel.isLaunched = true
    }
    LaunchedEffect(true) {
        viewModel.patient?.id?.let { id ->
            viewModel.getScheduledAppointmentsCount(id)
        }
    }
    BackHandler(enabled = true) {
        if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else navController.popBackStack()
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    val age = viewModel.patient?.birthDate?.toTimeInMilli()?.toAge()
                    val subTitle = "${
                        viewModel.patient?.gender?.get(0)?.uppercase()
                    }/$age · +91 ${viewModel.patient?.mobileNumber} ${if (viewModel.patient?.fhirId.isNullOrEmpty()) "" else " · ${viewModel.patient?.fhirId}"} "
                    Column {
                        Text(
                            text = NameConverter.getFullName(
                                viewModel.patient?.firstName,
                                viewModel.patient?.middleName,
                                viewModel.patient?.lastName
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("TITLE"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("SUBTITLE")
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {

                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            key = "patient_detailsID",
                            value = viewModel.patient?.id
                        )
                        navController.navigate(Screen.EditPatient.route)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile_icon),
                            contentDescription = "profile icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)) {
                    CardComposable(
                        navController,
                        viewModel.patient,
                        viewModel,
                        "APPOINTMENTS",
                        Screen.Appointments.route,
                        stringResource(id = R.string.appointments),
                        R.drawable.event_note_icon
                    )
                    CardComposable(
                        navController,
                        viewModel.patient,
                        viewModel,
                        "HOUSEHOLD_MEMBER",
                        Screen.HouseholdMembersScreen.route,
                        stringResource(id = R.string.household_members),
                        R.drawable.group_icon
                    )
                    CardComposable(
                        navController,
                        viewModel.patient,
                        viewModel,
                        "PRESCRIPTION",
                        Screen.Prescription.route,
                        stringResource(id = R.string.prescription),
                        R.drawable.prescriptions_icon
                    )
                }
                if (viewModel.showAllSlotsBookedDialog) {
                    AllSlotsBookedDialog {
                        viewModel.showAllSlotsBookedDialog = false
                    }
                }
            }
        },
        floatingActionButton = {
            viewModel.patient?.let { patient ->
                AppointmentsFab(
                    navController,
                    patient,
                    viewModel.isFabSelected,
                    viewModel.appointment,
                    viewModel.ifAlreadyWaiting
                ) { queueFabClicked ->
                    if (queueFabClicked) {
                        if (viewModel.appointment != null) {
                            // change status of patient to arrived and navigate to queue screen
                            viewModel.updateStatusToArrived {
                                CoroutineScope(Dispatchers.Main).launch {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        PATIENT_ARRIVED,
                                        true
                                    )
                                    navController.popBackStack()
                                }
                            }
                        } else {
                            // add patient to queue and navigate to queue screen
                            if (viewModel.ifAllSlotsBooked) {
                                viewModel.showAllSlotsBookedDialog = true
                            } else {
                                viewModel.addPatientToQueue {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        navController.previousBackStackEntry?.savedStateHandle?.set(
                                            ADD_TO_QUEUE,
                                            true
                                        )
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    }
                    viewModel.isFabSelected = !viewModel.isFabSelected
                }
            }
        }
    )
}

@Composable
fun CardComposable(
    navController: NavController,
    patient: PatientResponse?,
    viewModel: PatientLandingScreenViewModel,
    tag: String,
    route: String,
    label: String,
    icon: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(bottom = 20.dp)
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    patient
                )
                navController.navigate(route)
                viewModel.isFabSelected = false
            }
            .testTag(tag),
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = tag + "_ICON",
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(32.dp, 22.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    if (label == stringResource(id = R.string.appointments)) Text(
                        text = stringResource(
                            id = R.string.appointments_scheduled,
                            viewModel.appointmentsCount
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("NUMBER_OF_APPOINTMENTS")
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "RIGHT_ARROW")
        }
    }
}

@Composable
fun AllSlotsBookedDialog(closeDialog: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(id = R.string.all_slots_booked),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("DIALOG_TITLE")
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.all_slots_booked_desc),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog(true)
                },
                modifier = Modifier.testTag("POSITIVE_BTN")
            ) {
                Text(
                    stringResource(id = R.string.okay)
                )
            }
        }
    )
}