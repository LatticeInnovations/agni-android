package com.heartcare.agni.ui.historyandtests

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.ScrollableTabRowComposable
import com.heartcare.agni.ui.historyandtests.priordx.PriorDxView
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.prescription.photo.view.AppointmentCompletedDialog
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.PRIOR_DX_SAVED
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

    LaunchedEffect(Unit) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.getAppointmentInfo(callback = {})
            viewModel.isLaunched = true
        }
        if (navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(PRIOR_DX_SAVED) == true) {
            snackBarHostState.showSnackbar(message = context.getString(R.string.prior_dx_saved))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_taking_and_tests),
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
        bottomBar = {
            HistoryBottomAppBar(pagerState, coroutineScope, navController, viewModel)
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ScrollableTabRowComposable(tabs, pagerState) { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }

                HorizontalPager(state = pagerState) { index ->
                    when (index) {
                        0 -> PriorDxView(viewModel)
                        else -> Text(tabs[index], modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    )

    // Dialogs
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
    viewModel: HistoryTakingAndTestsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSystemInDarkTheme()) Black else White)
            .padding(12.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Add Button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.getAppointmentInfo(
                    callback = {
                        when {
                            viewModel.canAddAssessment -> navigateToAddScreen(
                                pagerState,
                                navController,
                                coroutineScope
                            )

                            viewModel.isAppointmentCompleted -> viewModel.showAppointmentCompletedDialog =
                                true

                            else -> viewModel.showAddToQueueDialog = true
                        }
                    }
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.add_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(getBtnText(pagerState.currentPage))
        }

        // Navigation Buttons
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
                enabled = pagerState.canScrollBackward
            ) {
                Text(stringResource(R.string.back))
            }

            OutlinedButton(
                onClick = {
                    if (pagerState.canScrollForward) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        // Done logic
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
}

@Composable
private fun getBtnText(page: Int): String {
    return when (page) {
        0 -> stringResource(R.string.add_prior_diagnosis)
        1 -> stringResource(R.string.add_medication)
        2 -> stringResource(R.string.add_family_history)
        3 -> stringResource(R.string.add_allergies)
        4 -> stringResource(R.string.add_risk_factor)
        5 -> stringResource(R.string.add_tobacco_cessation)
        else -> ""
    }
}

private fun navigateToAddScreen(
    pagerState: PagerState,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        when (pagerState.currentPage) {
            0 -> navController.navigate(Screen.AddPriorDxScreen.route)
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
                        navigateToAddScreen(pagerState, navController, coroutineScope)
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
                            navigateToAddScreen(pagerState, navController, coroutineScope)
                        }
                    )
                }
            }
        }
    )
}