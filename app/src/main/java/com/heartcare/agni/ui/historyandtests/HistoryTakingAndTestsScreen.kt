package com.heartcare.agni.ui.historyandtests

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.AppointmentCompletedDialog
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.Loader
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.common.ScrollableTabRowComposable
import com.heartcare.agni.ui.historyandtests.allergy.AllergyView
import com.heartcare.agni.ui.historyandtests.family.FamilyHistoryView
import com.heartcare.agni.ui.historyandtests.medication.MedicationView
import com.heartcare.agni.ui.historyandtests.priordx.PriorDxView
import com.heartcare.agni.ui.historyandtests.risk.view.RiskFactorsView
import com.heartcare.agni.ui.historyandtests.tobacco.view.TobaccoCessationView
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.ALLERGIES_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.FAMILY_HISTORY_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.MEDICATION_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.PRIOR_DX_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.RISK_FACTORS_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.TOBACCO_CESSATION_SAVED
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTakingAndTestsScreen(
    navController: NavController,
    viewModel: HistoryTakingAndTestsViewModel = hiltViewModel()
) {
    val tabs = stringArrayResource(R.array.history_and_tests_tabs).toList()
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { tabs.size }

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSite by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<RecordType?>(null) }

    BackHandler {
        if (currentStep > 0) {
            if (currentStep == 2) {
                currentStep = 1
            } else if (currentStep == 1) {
                currentStep = 0
            }
        } else {
            navController.navigateUp()
        }
    }

    HandleLaunchedEffectsAndSnackBars(
        viewModel = viewModel,
        navController = navController,
        snackBarHostState = snackBarHostState,
        context = context
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentStep > 0) "Add ${tabs[pagerState.currentPage]}" else stringResource(R.string.history_taking_and_tests),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (currentStep == 0) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentStep > 0) {
                        IconButton(onClick = { currentStep = 0 }) {
                            Icon(Icons.Default.Clear, contentDescription = "Close")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        bottomBar = {
            if (currentStep == 0) {
                HistoryBottomAppBar(
                    pagerState,
                    coroutineScope,
                    navController,
                    viewModel,
                    snackBarHostState,
                    context
                ) { currentStep = 1 }
            }
        },
        content = { paddingValues ->
            when(currentStep) {
                0 -> {
                    HistoryScaffoldContent(
                        tabs = tabs,
                        pagerState = pagerState,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        modifier = Modifier.padding(paddingValues),
                        navController = navController
                    )
                }
                1 -> RecordTypeSelectionContent(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    onContinueClick = {
                        if (selectedType == RecordType.FACILITY) {
                            handleAddButtonClick(
                                viewModel = viewModel,
                                pagerState = pagerState,
                                coroutineScope = coroutineScope,
                                navController = navController,
                                snackBarHostState = snackBarHostState,
                                context = context
                            )
                        } else if (selectedType == RecordType.SCREENING_SITE) {
                            currentStep = 2
                        }
                    }
                )
                2 -> ScreeningSiteListContent(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    sites = SITE_LIST,
                    selectedSite = selectedSite,
                    onSiteSelected = { selectedSite = it },
                    onBackClick = { currentStep = 1 },
                    onContinueClick = {
                        handleAddButtonClick(
                            viewModel = viewModel,
                            pagerState = pagerState,
                            coroutineScope = coroutineScope,
                            navController = navController,
                            snackBarHostState = snackBarHostState,
                            context = context
                        )
                    }
                )
            }
        }
    )

    HistoryDialogs(viewModel, navController, pagerState, coroutineScope)
}

@Composable
private fun HandleLaunchedEffectsAndSnackBars(
    viewModel: HistoryTakingAndTestsViewModel,
    navController: NavController,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.getAppointmentInfo(callback = {})
            viewModel.isLaunched = true
        }
        viewModel.getPreviousRecords(viewModel.patient!!.id)

        showSnackBars(navController, snackBarHostState, context)
    }
}

private suspend fun showSnackBars(
    navController: NavController,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
        if (handle.remove<Boolean>(PRIOR_DX_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.prior_dx_saved))
        }
        if (handle.remove<Boolean>(MEDICATION_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.medication_saved))
        }
        if (handle.remove<Boolean>(FAMILY_HISTORY_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.family_history_saved))
        }
        if (handle.remove<Boolean>(ALLERGIES_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.allergies_saved))
        }
        if (handle.remove<Boolean>(RISK_FACTORS_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.risk_factors_saved))
        }
        if (handle.remove<Boolean>(TOBACCO_CESSATION_SAVED) == true) {
            snackBarHostState.showSnackbar(context.getString(R.string.tobacco_cessation_saved))
        }
    }
}

@Composable
private fun HistoryScaffoldContent(
    tabs: List<String>,
    pagerState: PagerState,
    viewModel: HistoryTakingAndTestsViewModel,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(modifier = modifier) {
        ScrollableTabRowComposable(tabs, pagerState) { index ->
            coroutineScope.launch {
                pagerState.animateScrollToPage(index)
            }
        }

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Loader()
            }
        } else {
            HorizontalPager(state = pagerState) { index ->
                when (index) {
                    0 -> PriorDxView(viewModel)
                    1 -> MedicationView(viewModel)
                    2 -> FamilyHistoryView(viewModel)
                    3 -> AllergyView(viewModel)
                    4 -> RiskFactorsView(viewModel, navController)
                    5 -> TobaccoCessationView(viewModel, navController)
                }
            }
        }
    }
}

@Composable
private fun HistoryDialogs(
    viewModel: HistoryTakingAndTestsViewModel,
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(viewModel, navController, pagerState, coroutineScope)
    }

    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }

    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
}

@Composable
private fun HistoryBottomAppBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    viewModel: HistoryTakingAndTestsViewModel,
    snackBarHostState: SnackbarHostState,
    context: Context,
    onAddClick: () -> Unit
) {
    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) Black else White)
                .padding(12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AddAssessmentButton(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                viewModel = viewModel,
                snackBarHostState = snackBarHostState,
                context = context,
                navController=navController,
                onAddClick = onAddClick
            )

            NavigationButtons(
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                navController = navController
            )
        }
    }
}

@Composable
private fun AddAssessmentButton(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    viewModel: HistoryTakingAndTestsViewModel,
    snackBarHostState: SnackbarHostState,
    context: Context,
    navController: NavController,
    onAddClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                val hasData = when (pagerState.currentPage) {
                    0 -> viewModel.todayPriorDx != null
                    1 -> viewModel.todayHistoryMedication != null
                    2 -> viewModel.todayFamilyHistory != null
                    3 -> viewModel.todayAllergy != null
                    4 -> viewModel.todayRiskFactor != null
                    5 -> viewModel.todayTobaccoCessation != null
                    else -> false
                }
                if (hasData && !viewModel.existsInOtherHospital) {
                    handleAddButtonClick(
                        viewModel = viewModel,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        navController = navController,
                        snackBarHostState = snackBarHostState,
                        context = context
                    )
                } else {
                    onAddClick()
                }
            } else {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        context.getString(R.string.patient_deceased_error_msg)
                    )
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.add_icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(getBtnText(pagerState.currentPage, viewModel))
    }
}

private fun handleAddButtonClick(
    viewModel: HistoryTakingAndTestsViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    viewModel.getAppointmentInfo(
        callback = {
            when {
                viewModel.existsInOtherHospital -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.appointment_exists_in_other_hospital)
                        )
                    }
                }

                viewModel.canAddAssessment -> {
                    navigateToAddScreen(
                        viewModel.patient!!,
                        pagerState,
                        navController,
                        coroutineScope
                    )
                }

                viewModel.isAppointmentCompleted -> {
                    viewModel.showAppointmentCompletedDialog = true
                }

                else -> {
                    viewModel.showAddToQueueDialog = true
                }
            }
        }
    )
}

@Composable
private fun NavigationButtons(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            modifier = Modifier.weight(1f),
            enabled = pagerState.canScrollBackward,
            border = if (!pagerState.canScrollBackward)
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            else ButtonDefaults.outlinedButtonBorder()
        ) {
            Text(stringResource(R.string.back))
        }

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.canScrollForward) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        // Done logic here
                        navController.navigateUp()
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (pagerState.canScrollForward)
                    stringResource(R.string.next)
                else stringResource(R.string.done)
            )
        }
    }
}

@Composable
private fun getBtnText(
    page: Int,
    viewModel: HistoryTakingAndTestsViewModel
): String {
    val context = LocalContext.current

    val pageMapping = mapOf(
        0 to Pair(R.string.add_prior_diagnosis, R.string.update_prior_diagnosis),
        1 to Pair(R.string.add_medication, R.string.update_medication),
        2 to Pair(R.string.add_family_history, R.string.update_family_history),
        3 to Pair(R.string.add_allergies, R.string.update_allergies),
        4 to Pair(R.string.add_risk_factor, R.string.update_risk_factor),
        5 to Pair(R.string.add_tobacco_cessation, R.string.update_tobacco_cessation)
    )

    val (addRes, updateRes) = pageMapping[page] ?: return ""

    val hasData = when (page) {
        0 -> viewModel.todayPriorDx != null
        1 -> viewModel.todayHistoryMedication != null
        2 -> viewModel.todayFamilyHistory != null
        3 -> viewModel.todayAllergy != null
        4 -> viewModel.todayRiskFactor != null
        5 -> viewModel.todayTobaccoCessation != null
        else -> false
    }

    return if (hasData && !viewModel.existsInOtherHospital) {
        context.getString(updateRes)
    } else {
        context.getString(addRes)
    }
}

private fun navigateToAddScreen(
    patient: PatientResponse,
    pagerState: PagerState,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        navController.currentBackStackEntry?.savedStateHandle?.set(PATIENT, patient)
        when (pagerState.currentPage) {
            0 -> navController.navigate(Screen.AddPriorDxScreen.route)
            1 -> navController.navigate(Screen.AddMedicationScreen.route)
            2 -> navController.navigate(Screen.AddFamilyHistoryScreen.route)
            3 -> navController.navigate(Screen.AddAllergyScreen.route)
            4 -> navController.navigate(Screen.AddRiskFactorsScreen.route)
            5 -> navController.navigate(Screen.AddTobaccoCessationScreen.route)
        }
    }
}

@Composable
private fun AddToQueueDialog(
    viewModel: HistoryTakingAndTestsViewModel,
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    CustomDialog(
        title = stringResource(
            if (viewModel.appointment != null) R.string.patient_arrived_question else R.string.add_to_queue_question
        ),
        text = stringResource(R.string.add_to_queue_assessment_dialog_description),
        dismissBtnText = stringResource(R.string.dismiss),
        confirmBtnText = stringResource(
            if (viewModel.appointment != null) R.string.mark_arrived else R.string.add_to_queue
        ),
        dismiss = { viewModel.showAddToQueueDialog = false },
        confirm = {
            if (viewModel.appointment != null) {
                viewModel.updateStatusToArrived(
                    patient = viewModel.patient!!,
                    appointment = viewModel.appointment!!,
                    updated = {
                        viewModel.showAddToQueueDialog = false
                        navigateToAddScreen(
                            viewModel.patient!!,
                            pagerState,
                            navController,
                            coroutineScope
                        )
                    }
                )
            } else {
                if (viewModel.ifAllSlotsBooked) {
                    viewModel.showAllSlotsBookedDialog = true
                } else {
                    viewModel.addPatientToQueue(
                        viewModel.patient!!,
                        addedToQueue = {
                            viewModel.showAddToQueueDialog = false
                            navigateToAddScreen(
                                viewModel.patient!!,
                                pagerState,
                                navController,
                                coroutineScope
                            )
                        }
                    )
                }
            }
        }
    )
}