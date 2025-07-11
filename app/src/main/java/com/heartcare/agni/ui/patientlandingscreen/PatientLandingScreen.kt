package com.heartcare.agni.ui.patientlandingscreen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.PhotoUploadTypeEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.BottomNavBar
import com.heartcare.agni.ui.common.appointmentsfab.AppointmentsFab
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.heartcare.agni.utils.constants.PhotoUploadViewType.PHOTO_VIEW_TYPE
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PatientLandingScreen(
    navController: NavController,
    viewModel: PatientLandingScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.selectedIndex =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>(
                    SELECTED_INDEX
                )!!
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
            viewModel.getUploadsCount(id)
            viewModel.getLastCVDRisk(id)
            viewModel.getImmunizationRecommendationList(id)
        }
    }
    BackHandler(enabled = true) {
        if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else navController.navigateUp()
    }
    viewModel.patient?.let {
        Scaffold(
            topBar = {
                AppBarComposable(viewModel, navController)
            },
            content = {
                Box(modifier = Modifier.padding(it)) {
                    CardComposableList(viewModel, navController, scope, snackbarHostState, context)
                    if (viewModel.showAllSlotsBookedDialog) {
                        AllSlotsBookedDialog {
                            viewModel.showAllSlotsBookedDialog = false
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavBar(
                    selectedIndex = viewModel.selectedIndex,
                    updateIndex = { index ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            SELECTED_INDEX,
                            index
                        )
                        navController.navigate(Screen.LandingScreen.route)
                    }
                )
            }
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 160.dp)
                .navigationBarsPadding()
        )
    }
    viewModel.patient?.let { patient ->
        AppointmentsFab(
            modifier = Modifier.padding(bottom = 80.dp, end = 16.dp),
            navController,
            patient,
            viewModel.isFabSelected
        ) { showDialog ->
            if (showDialog) {
                viewModel.showAllSlotsBookedDialog = true
            } else viewModel.isFabSelected = !viewModel.isFabSelected
        }
    }
}

@Composable
private fun CardComposableList(
    viewModel: PatientLandingScreenViewModel,
    navController: NavController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        CardComposable(
            viewModel,
            stringResource(id = R.string.household_members),
            R.drawable.group_icon,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    viewModel.patient
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    SELECTED_INDEX,
                    viewModel.selectedIndex
                )
                navController.navigate(Screen.HouseholdMembersScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.appointments),
            R.drawable.event_note_icon,
            stringResource(
                id = R.string.appointments_scheduled,
                viewModel.appointmentsCount,
                viewModel.pastAppointmentsCount
            ),
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    viewModel.patient
                )
                navController.navigate(Screen.Appointments.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.cvd_risk_assessment),
            R.drawable.cardiology,
            if (viewModel.cvdRisk.isBlank()) null
            else stringResource(R.string.percentage, viewModel.cvdRisk),
            isCardDisabled = viewModel.patient!!.gender == "other" ||
                    viewModel.patient!!.birthDate.toTimeInMilli()
                        .toAge() !in 40..74,
            onClick = {
                if (viewModel.patient!!.gender == "other" ||
                    viewModel.patient!!.birthDate.toTimeInMilli().toAge() !in 40..74
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.cvd_error_message)
                        )
                    }
                } else {
                    scope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient",
                            viewModel.patient
                        )
                        navController.navigate(Screen.CVDRiskAssessmentScreen.route)
                    }
                }

            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.vital),
            R.drawable.vital_signs,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.VitalsScreen.route)
            }
        )

        CardComposable(
            viewModel,
            stringResource(id = R.string.symptoms_and_diagnosis),
            R.drawable.diagnosis,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.SymptomsAndDiagnosisScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.prescription),
            R.drawable.prescriptions_icon,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    viewModel.patient
                )
                navController.navigate(Screen.PrescriptionPhotoViewScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.drugs_dispense),
            R.drawable.pill,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.DrugDispenseScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.lab_test),
            R.drawable.lab_research,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PHOTO_VIEW_TYPE,
                    PhotoUploadTypeEnum.LAB_TEST.value
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.LabAndMedRecordPhotoViewScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.medical_record),
            R.drawable.contract,
            null,
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PHOTO_VIEW_TYPE,
                    PhotoUploadTypeEnum.MEDICAL_RECORD.value
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.LabAndMedRecordPhotoViewScreen.route)
            }
        )
        CardComposable(
            viewModel,
            stringResource(id = R.string.vaccination),
            R.drawable.syringe,
            stringResource(
                id = R.string.vaccination_info,
                viewModel.upcomingVaccine,
                viewModel.missedVaccine,
                viewModel.takenVaccine
            ),
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    viewModel.patient
                )
                navController.navigate(Screen.VaccinationScreen.route)
            }
        )
        Spacer(
            modifier = Modifier.height(76.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBarComposable(
    viewModel: PatientLandingScreenViewModel,
    navController: NavController
) {
    LargeTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        ),
        navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "BACK_ICON"
                )
            }
        },
        title = {
            val age = viewModel.patient?.birthDate?.toTimeInMilli()?.toAge()
            val subTitle = "${
                viewModel.patient?.gender?.get(0)?.uppercase()
            }/$age · ${viewModel.patient?.mobileNumber} ${if (viewModel.patient?.fhirId.isNullOrEmpty()) "" else " · ${viewModel.patient?.fhirId}"} "
            Column {
                Text(
                    text = NameConverter.getFullName(
                        viewModel.patient?.firstName,
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
}

@Composable
private fun CardComposable(
    viewModel: PatientLandingScreenViewModel,
    label: String,
    icon: Int,
    subText: String?,
    isCardDisabled: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isCardDisabled) MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                else MaterialTheme.colorScheme.surface
            )
            .clickable {
                viewModel.isFabSelected = false
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 14.dp, end = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = icon.toString(),
                tint = if (isCardDisabled) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.surfaceTint,
                modifier = Modifier.size(32.dp, 22.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            HeaderComposable(label, subText, isCardDisabled)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "RIGHT_ARROW",
                tint = if (isCardDisabled) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.onSurface
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun HeaderComposable(
    label: String,
    subText: String?,
    isCardDisabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCardDisabled) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.onSurface
        )
        if (subText != null) {
            Text(
                text = subText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCardDisabled) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
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