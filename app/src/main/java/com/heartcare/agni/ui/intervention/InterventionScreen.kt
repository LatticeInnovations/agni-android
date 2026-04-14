package com.heartcare.agni.ui.intervention

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
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
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.utils.constants.NavControllerConstants.INTERVENTIONS_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionScreen(
    navController: NavController,
    viewModel: InterventionViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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

    HandleLaunchedEffect(navController, viewModel, snackBarHostState, context)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.interventions),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (currentStep) {
                    0 -> InterventionContent(viewModel)
                    1 -> RecordTypeSelectionContent(
                        modifier = Modifier.fillMaxSize(),
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it },
                        onContinueClick = {
                            if (selectedType == RecordType.FACILITY) {
                                handleAddInterventionLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                            } else if (selectedType == RecordType.SCREENING_SITE) {
                                currentStep = 2
                            }
                        }
                    )
                    2 -> ScreeningSiteListContent(
                        modifier = Modifier.fillMaxSize(),
                        sites = SITE_LIST,
                        selectedSite = selectedSite,
                        onSiteSelected = { selectedSite = it },
                        onBackClick = { currentStep = 1 },
                        onContinueClick = {
                            handleAddInterventionLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (currentStep == 0) {
                InterventionBottomBar(
                    viewModel = viewModel,
                    onClickAdd = {
                        if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                            if (viewModel.todayIntervention != null && !viewModel.existsInOtherHospital) {
                                handleAddInterventionLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                            } else {
                                currentStep = 1
                            }
                        } else {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    context.getString(R.string.patient_deceased_error_msg)
                                )
                            }
                        }
                    }
                )
            }
        }
    )

    InterventionDialogs(navController, viewModel, coroutineScope)
}

@Composable
private fun HandleLaunchedEffect(
    navController: NavController,
    viewModel: InterventionViewModel,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.getAppointmentInfo { }
            viewModel.isLaunched = true
        }
        viewModel.getInterventionRecords(viewModel.patient!!.id)
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            if (handle.remove<Boolean>(INTERVENTIONS_SAVED) == true) {
                snackBarHostState.showSnackbar(
                    context.getString(
                        if (viewModel.todayIntervention == null) R.string.interventions_saved
                        else R.string.interventions_updated
                    )
                )
            }
        }
    }
}

@Composable
private fun InterventionContent(
    viewModel: InterventionViewModel
) {
    if (viewModel.interventionLists.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.no_record_found),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(2f))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.interventionLists.forEach { intervention ->
                    ExpandableCard(
                        createdOn = intervention.appUpdatedDate,
                        practitionerName = intervention.practitionerName,
                        listOfItems = intervention.interventions.map { "${it.code} ${it.display}" },
                        isBulleted = true
                    )
                }
            }
        }
    }
}

private fun handleAddInterventionLogic(
    viewModel: InterventionViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
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
                    coroutineScope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient
                        )
                        navController.navigate(Screen.AddInterventionScreen.route)
                    }
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
private fun InterventionBottomBar(
    viewModel: InterventionViewModel,
    onClickAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(0.5.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            onClick = onClickAdd
        ) {
            Icon(Icons.Filled.Add, Icons.Filled.Add.name)
            Spacer(Modifier.width(6.dp))
            Text(
                stringResource(
                    id = if (viewModel.todayIntervention == null || viewModel.existsInOtherHospital) R.string.add_interventions
                    else R.string.update_interventions
                )
            )
        }
    }
}

@Composable
private fun InterventionDialogs(
    navController: NavController,
    viewModel: InterventionViewModel,
    coroutineScope: CoroutineScope
) {
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(viewModel, navController, coroutineScope)
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
private fun AddToQueueDialog(
    viewModel: InterventionViewModel,
    navController: NavController,
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
                        coroutineScope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                PATIENT,
                                viewModel.patient
                            )
                            navController.navigate(Screen.AddInterventionScreen.route)
                        }
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
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    viewModel.patient
                                )
                                navController.navigate(Screen.AddInterventionScreen.route)
                            }
                        }
                    )
                }
            }
        }
    )
}