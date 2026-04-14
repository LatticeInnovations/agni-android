package com.heartcare.agni.ui.referral

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.AppointmentCompletedDialog
import com.heartcare.agni.ui.common.CardWithRightArrow
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.REFERRAL
import com.heartcare.agni.utils.constants.NavControllerConstants.REFERRAL_SAVED
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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

    HandleLaunchedEffect(viewModel, navController, snackBarHostState, context)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.referrals),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                when (currentStep) {
                    0 -> ReferralScreenContent(viewModel, navController)
                    1 -> RecordTypeSelectionContent(
                        modifier = Modifier.fillMaxSize(),
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it },
                        onContinueClick = {
                            if (selectedType == RecordType.FACILITY) {
                                handleAddReferralLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
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
                            handleAddReferralLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (currentStep == 0) {
                ReferralBottomBar(
                    viewModel = viewModel,
                    onClickAdd = {
                        if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                            if (viewModel.todayReferral != null && !viewModel.existsInOtherHospital) {
                                handleAddReferralLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
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
    Dialogs(viewModel, navController, coroutineScope)
}


@Composable
private fun HandleLaunchedEffect(
    viewModel: ReferralViewModel,
    navController: NavController,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                PATIENT
            )?.let {
                viewModel.patient = it
                viewModel.getReferralRecords(it.id)
            }
            viewModel.getAppointmentInfo(callback = {})
            viewModel.isLaunched = true
        }
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            if (handle.remove<Boolean>(REFERRAL_SAVED) == true) {
                viewModel.getReferralRecords(viewModel.patient!!.id)
                snackBarHostState.showSnackbar(
                    context.getString(
                        if (viewModel.todayReferral == null) R.string.referral_added_successfully
                        else R.string.referral_updated_successfully
                    )
                )
            }
        }
    }
}

@Composable
private fun ReferralScreenContent(
    viewModel: ReferralViewModel,
    navController: NavController
) {
    if (viewModel.referralList.isEmpty()) {
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
                Text(
                    text = stringResource(R.string.recent_referral),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                viewModel.referralList.forEach { referral ->
                    CardWithRightArrow(
                        date = referral.appUpdatedDate,
                        practitionerName = referral.practitionerName,
                        onClick = {
                            // navigate to view referral
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                REFERRAL,
                                referral
                            )
                            navController.navigate(Screen.ViewReferralScreen.route)
                        }
                    )
                }
            }
        }
    }
}

private fun handleAddReferralLogic(
    viewModel: ReferralViewModel,
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
                        navController.navigate(Screen.AddReferralScreen.route)
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
private fun ReferralBottomBar(
    viewModel: ReferralViewModel,
    onClickAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .navigationBarsPadding()
    ) {
        Button(
            onClick = onClickAdd,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.add_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (viewModel.todayReferral == null || viewModel.existsInOtherHospital) stringResource(R.string.add_referral)
                else stringResource(R.string.update_referral)
            )
        }
    }
}

@Composable
private fun Dialogs(
    viewModel: ReferralViewModel,
    navController: NavController,
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
    viewModel: ReferralViewModel,
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
                            navController.navigate(Screen.AddReferralScreen.route)
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
                                navController.navigate(Screen.AddReferralScreen.route)
                            }
                        }
                    )
                }
            }
        }
    )
}