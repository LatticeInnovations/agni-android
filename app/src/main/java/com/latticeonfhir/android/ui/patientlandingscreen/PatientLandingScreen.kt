package com.latticeonfhir.android.ui.patientlandingscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.navigation.NavController
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import org.hl7.fhir.r4.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLandingScreen(
    navController: NavController,
    viewModel: PatientLandingScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<Patient>(
                    PATIENT
                )!!
        }
        viewModel.patient = viewModel.getPatientData()
        viewModel.getScheduledAppointmentsCount()
        viewModel.isLaunched = true
    }
    BackHandler(enabled = true) {
        if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else navController.popBackStack()
    }
    if (viewModel.isLaunched) {
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                        }
                    },
                    title = {
                        val age = viewModel.patient.birthDate.time.toAge()
                        val subTitle = "${
                            viewModel.patient.gender.display
                        }/$age · +91 ${viewModel.patient.telecom[0].value}"
                        Column {
                            Text(
                                text = viewModel.patient.nameFirstRep.nameAsSingleString,
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
                                value = viewModel.patient.logicalId
                            )
                            navController.navigate(Screen.PatientProfile.route)
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
                // TODO: to be implemented after appointment screen binding
//            viewModel.patient?.let { patient ->
//                AppointmentsFab(
//                    navController,
//                    patient,
//                    viewModel.isFabSelected
//                ) { showDialog ->
//                    if (showDialog) {
//                        viewModel.showAllSlotsBookedDialog = true
//                    } else viewModel.isFabSelected = !viewModel.isFabSelected
//                }
//            }
            }
        )
    }
}

@Composable
fun CardComposable(
    navController: NavController,
    patient: Patient,
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
                    PATIENT,
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
                            viewModel.appointmentsIds.size
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("NUMBER_OF_APPOINTMENTS")
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "RIGHT_ARROW")
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