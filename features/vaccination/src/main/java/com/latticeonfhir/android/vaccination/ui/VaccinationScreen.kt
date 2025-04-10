package com.latticeonfhir.core.vaccination.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.android.theme.TakenLabel
import com.latticeonfhir.android.theme.TakenLabelDark
import com.latticeonfhir.android.ui.AddToQueueDialog
import com.latticeonfhir.android.ui.AllSlotsBookedDialog
import com.latticeonfhir.core.ui.AppointmentCompletedDialog
import com.latticeonfhir.core.ui.TabRowComposable
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE_ADDED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.VACCINE_ERROR_TYPE
import com.latticeonfhir.core.utils.converters.TimeConverter.daysBetween
import com.latticeonfhir.android.utils.converters.TimeConverter.formatAgeInDaysWeeksMonthsYears
import com.latticeonfhir.core.utils.converters.TimeConverter.convertStringToDate
import com.latticeonfhir.core.utils.converters.TimeConverter.plusMinusDays
import com.latticeonfhir.core.utils.converters.TimeConverter.toPrescriptionDate
import com.latticeonfhir.android.vaccination.R
import com.latticeonfhir.core.vaccination.data.enums.VaccineErrorTypeEnum
import com.latticeonfhir.core.vaccination.ui.VaccinationViewModel.Companion.MISSED
import com.latticeonfhir.core.vaccination.ui.VaccinationViewModel.Companion.TAKEN
import com.latticeonfhir.core.vaccination.ui.tabs.AllVaccinationScreen
import com.latticeonfhir.core.vaccination.ui.tabs.MissedVaccinationScreen
import com.latticeonfhir.android.vaccination.ui.tabs.TakenVaccinationScreen
import com.latticeonfhir.android.vaccination.utils.VaccinesUtils.getNumberWithOrdinalIndicator
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    BackHandler {
        if (pagerState.currentPage > 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else navController.navigateUp()
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
                if(viewModel.immunizationRecommendationList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = if (isSystemInDarkTheme()) Color.Black else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        VaccineEmptyScreen(
                            stringResource(R.string.fetching_immunization_recommendations),
                            stringResource(R.string.fetching_immunization_recommendations_info)
                        )
                    }
                } else {
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
            }
        },
        floatingActionButton = {
            if(viewModel.immunizationRecommendationList.isNotEmpty()) {
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
                                viewModel.selectedVaccine = null
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
        }
    )

    if (viewModel.showAllSlotsBookedDialog) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }

    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(
            appointment = viewModel.appointment,
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!,
                        viewModel.appointment!!
                    ) {
                        viewModel.showAddToQueueDialog = false
                        coroutineScope.launch {
                            if (viewModel.selectedVaccine == null) {
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
                            } else {
                                navigateToAddVaccine(
                                    navController,
                                    viewModel.selectedVaccine!!,
                                    viewModel.patient!!,
                                    viewModel.immunizationRecommendationList
                                )
                            }
                        }
                    }
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(viewModel.patient!!) {
                            viewModel.showAddToQueueDialog = false
                            coroutineScope.launch {
                                if (viewModel.selectedVaccine == null) {
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
                                } else {
                                    navigateToAddVaccine(
                                        navController,
                                        viewModel.selectedVaccine!!,
                                        viewModel.patient!!,
                                        viewModel.immunizationRecommendationList
                                    )
                                }
                            }
                        }
                    }
                }
            },
            dismiss = { viewModel.showAddToQueueDialog = false }
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
    vaccine: ImmunizationRecommendation,
    onClick: () -> Unit
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
                    onClick()
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
                text = if (missedOrTaken == TAKEN) vaccine.takenOn!!.toPrescriptionDate()
                    else vaccine.vaccineStartDate.toPrescriptionDate(),
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
