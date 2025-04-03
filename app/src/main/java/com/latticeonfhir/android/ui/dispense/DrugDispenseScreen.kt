package com.latticeonfhir.android.ui.dispense

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.DispenseCategoryEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.CustomDialog
import com.latticeonfhir.android.ui.TabRowComposable
import com.latticeonfhir.android.ui.dispense.log.DispenseLogScreen
import com.latticeonfhir.android.ui.dispense.prescription.PrescriptionTabScreen
import com.latticeonfhir.android.ui.dispense.prescription.ViewRXScreen
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.ui.prescription.photo.view.AppointmentCompletedDialog
import com.latticeonfhir.android.utils.constants.NavControllerConstants.OTC_DISPENSED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DrugDispenseScreen(
    navController: NavController,
    viewModel: DrugDispenseViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }
    viewModel.isOTCDispensed =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(OTC_DISPENSED) == true
    BackHandler {
        if (pagerState.currentPage == 1) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else if (viewModel.prescriptionSelected != null) viewModel.prescriptionSelected = null
        else navController.navigateUp()
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
        }
        viewModel.getPrescriptionDispenseData(viewModel.patient!!.id)
        if (viewModel.isOTCDispensed) {
            pagerState.animateScrollToPage(1)
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(OTC_DISPENSED)
            viewModel.isOTCDispensed = false
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.drugs_dispense_heading),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = {
                            viewModel.getAppointmentInfo (
                                callback = {
                                    if (viewModel.canAddDispense) {
                                        coroutineScope.launch {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                PATIENT,
                                                viewModel.patient
                                            )
                                            navController.navigate(Screen.OTCScreen.route)
                                        }
                                    } else if (viewModel.isAppointmentCompleted) {
                                        viewModel.showAppointmentCompletedDialog = true
                                    } else {
                                        viewModel.categoryClicked = DispenseCategoryEnum.OTC.value
                                        viewModel.showAddToQueueDialog = true
                                    }
                                }
                            )
                        }
                    ) {
                        Text(stringResource(R.string.otc))
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    com.latticeonfhir.android.ui.TabRowComposable(
                        viewModel.tabs,
                        pagerState
                    ) { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                index
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> PrescriptionTabScreen(navController, viewModel)
                            1 -> DispenseLogScreen(viewModel)
                        }
                    }
                }
            }
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = viewModel.prescriptionSelected != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ViewRXScreen(viewModel)
        }
    }
    if (viewModel.showAddToQueueDialog) {
        com.latticeonfhir.android.ui.CustomDialog(
            title = if (viewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
                id = R.string.add_to_queue_question
            ),
            text = stringResource(id = R.string.add_to_queue_assessment_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (viewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { viewModel.showAddToQueueDialog = false },
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!,
                        viewModel.appointment!!,
                        updated = {
                            viewModel.showAddToQueueDialog = false
                            if (viewModel.categoryClicked == DispenseCategoryEnum.PRESCRIBED.value) {
                                coroutineScope.launch {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "prescription_id",
                                        viewModel.prescriptionToDispense!!.prescription.id
                                    )
                                    navController.navigate(Screen.DispensePrescriptionScreen.route)
                                }
                            } else {
                                coroutineScope.launch {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        PATIENT,
                                        viewModel.patient
                                    )
                                    navController.navigate(Screen.OTCScreen.route)
                                }
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
                                if (viewModel.categoryClicked == DispenseCategoryEnum.PRESCRIBED.value) {
                                    coroutineScope.launch {
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "prescription_id",
                                            viewModel.prescriptionToDispense!!.prescription.id
                                        )
                                        navController.navigate(Screen.DispensePrescriptionScreen.route)
                                    }
                                } else {
                                    coroutineScope.launch {
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            PATIENT,
                                            viewModel.patient
                                        )
                                        navController.navigate(Screen.OTCScreen.route)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
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