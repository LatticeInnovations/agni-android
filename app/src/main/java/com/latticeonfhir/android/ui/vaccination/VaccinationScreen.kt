package com.latticeonfhir.android.ui.vaccination

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.VaccineErrorTypeEnum
import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.CustomDialog
import com.latticeonfhir.android.ui.common.TabRowComposable
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.ui.prescription.photo.view.AppointmentCompletedDialog
import com.latticeonfhir.android.ui.theme.TakenLabel
import com.latticeonfhir.android.ui.theme.TakenLabelDark
import com.latticeonfhir.android.ui.vaccination.VaccinationViewModel.Companion.MISSED
import com.latticeonfhir.android.ui.vaccination.tabs.AllVaccinationScreen
import com.latticeonfhir.android.ui.vaccination.tabs.MissedVaccinationScreen
import com.latticeonfhir.android.ui.vaccination.tabs.TakenVaccinationScreen
import com.latticeonfhir.android.ui.vaccination.utils.VaccinesUtils.getNumberWithOrdinalIndicator
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.NavControllerConstants.VACCINE
import com.latticeonfhir.android.utils.constants.NavControllerConstants.VACCINE_ADDED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.VACCINE_ERROR_TYPE
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertStringToDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.daysBetween
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.formatAgeInDaysWeeksMonthsYears
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.plusMinusDays
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    navController: NavController,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }
    viewModel.isVaccineAdded =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(VACCINE_ADDED) == true
    LaunchedEffect(viewModel.isLaunched) {
        if (viewModel.isVaccineAdded) {
            viewModel.getImmunizationRecommendationAndImmunizationList(viewModel.patient!!.id)
            viewModel.isVaccineAdded = false
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(VACCINE_ADDED)
            snackbarHostState.showSnackbar(
                context.getString(R.string.vaccination_added_successfully)
            )
        }
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.patient?.let {
                viewModel.getImmunizationRecommendationAndImmunizationList(it.id)
            }
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    Text(stringResource(R.string.vaccination))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRowComposable(
                        viewModel.tabs,
                        pagerState
                    ) { index ->
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> {
                                AllVaccinationScreen(viewModel, navController)
                            }

                            1 -> {
                                MissedVaccinationScreen(navController, viewModel)
                            }

                            2 -> {
                                TakenVaccinationScreen(navController, viewModel)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.getAppointmentInfo {
                        if (viewModel.canAddVaccination) {
                            // navigate to add vaccination screen
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    viewModel.patient
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    VACCINE,
                                    null
                                )
                                navController.navigate(Screen.AddVaccinationScreen.route)
                            }
                        } else if (viewModel.isAppointmentCompleted) {
                            viewModel.showAppointmentCompletedDialog = true
                        } else {
                            viewModel.showAddToQueueDialog = true
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = null,
                    Modifier.size(24.dp)
                )
            }
        }
    )

    if (viewModel.showAllSlotsBookedDialog) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }

    if (viewModel.showAddToQueueDialog) {
        CustomDialog(
            title = if (viewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
                id = R.string.add_to_queue_question
            ),
            text = stringResource(id = R.string.add_to_queue_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (viewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { viewModel.showAddToQueueDialog = false },
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!,
                        viewModel.appointment!!
                    ) {
                        viewModel.showAddToQueueDialog = false
                        coroutineScope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                PATIENT,
                                viewModel.patient
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                VACCINE,
                                viewModel.selectedVaccine
                            )
                            navController.navigate(Screen.AddVaccinationScreen.route)
                        }
                    }
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(viewModel.patient!!) {
                            viewModel.showAddToQueueDialog = false
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    viewModel.patient
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    VACCINE,
                                    viewModel.selectedVaccine
                                )
                                navController.navigate(Screen.AddVaccinationScreen.route)
                            }
                        }
                    }
                }
            }
        )
    }
    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
}

@Composable
fun AgeComposable(viewModel: VaccinationViewModel) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
    ) {
        viewModel.patient?.let { patient ->
            Text(
                text = stringResource(
                    R.string.age_string,
                    formatAgeInDaysWeeksMonthsYears(patient.birthDate)
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun VaccineEmptyScreen(
    msg: String,
    info: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 200.dp, horizontal = 64.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = msg,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        Text(
            text = info,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outlineVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VaccineCard(
    missedOrTaken: String,
    navController: NavController,
    patient: PatientResponse,
    vaccine: ImmunizationRecommendation,
    listOfAllVaccinations: List<ImmunizationRecommendation>
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceBright,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (missedOrTaken == MISSED) {
                        navigateToAddVaccine(navController, vaccine, patient, listOfAllVaccinations)
                    } else {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            patient
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            VACCINE,
                            vaccine
                        )
                        navController.navigate(Screen.ViewVaccinationScreen.route)
                    }
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = vaccine.name.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = vaccine.shortName.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(
                            R.string.number_dose,
                            vaccine.doseNumber.getNumberWithOrdinalIndicator()
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight.name,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = vaccine.vaccineStartDate.toPrescriptionDate(),
                style = MaterialTheme.typography.labelMedium,
                color = if (missedOrTaken == MISSED) MaterialTheme.colorScheme.error
                else {
                    if (isSystemInDarkTheme()) TakenLabelDark
                    else TakenLabel
                }
            )
        }
    }
}

fun navigateToAddVaccine(
    navController: NavController,
    vaccine: ImmunizationRecommendation,
    patient: PatientResponse,
    listOfAllVaccinations: List<ImmunizationRecommendation>
) {
    if (listOfAllVaccinations.filterList { name == vaccine.name && doseNumber < vaccine.doseNumber && takenOn == null }
            .isNotEmpty()) {
        navController.currentBackStackEntry?.savedStateHandle?.set(
            VACCINE_ERROR_TYPE, VaccineErrorTypeEnum.DOSE.errorType
        )
        navController.navigate(Screen.VaccinationErrorScreen.route)
    } else {
        val timeRange = if (daysBetween(
                patient.birthDate.convertStringToDate(),
                vaccine.vaccineStartDate
            ) <= 90
        ) {
            vaccine.vaccineStartDate.plusMinusDays(-3)..vaccine.vaccineEndDate.plusMinusDays(
                3
            )
        } else {
            vaccine.vaccineStartDate.plusMinusDays(-15)..vaccine.vaccineEndDate.plusMinusDays(
                15
            )
        }
        if (Date() in timeRange) {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                PATIENT,
                patient
            )
            navController.currentBackStackEntry?.savedStateHandle?.set(
                VACCINE,
                vaccine
            )
            navController.navigate(Screen.AddVaccinationScreen.route)
        } else {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                VACCINE_ERROR_TYPE, VaccineErrorTypeEnum.TIME.errorType
            )
            navController.navigate(Screen.VaccinationErrorScreen.route)
        }
    }
}
