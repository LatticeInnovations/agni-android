@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.latticeonfhir.android.ui.appointments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.AppointmentsFab
import com.latticeonfhir.android.ui.common.customTabIndicatorOffset
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    navController: NavController,
    viewModel: AppointmentsScreenViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(viewModel.tabs.size) {
            tabWidthStateList.add(0.dp)
        }
        tabWidthStateList
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    viewModel.rescheduled = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
        NavControllerConstants.RESCHEDULED
    ) == true
    viewModel.scheduled = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
        NavControllerConstants.SCHEDULED
    ) == true

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
    }
    LaunchedEffect(true) {
        viewModel.patient?.id?.let { patientId ->
            viewModel.getAppointmentsList(patientId)
        }
        if (viewModel.rescheduled){
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.appointment_rescheduled)
                )
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                NavControllerConstants.RESCHEDULED
            )
        }
        if (viewModel.scheduled){
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.appointment_scheduled)
                )
            }
            navController.previousBackStackEntry?.savedStateHandle?.remove<Boolean>(
                NavControllerConstants.SCHEDULED
            )
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.appointments),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.testTag("TABS"),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.customTabIndicatorOffset(
                                    currentTabPosition = tabPositions[pagerState.currentPage],
                                    tabWidth = tabWidths[pagerState.currentPage]
                                )
                            )
                        }
                    ) {
                        viewModel.tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(title,
                                        onTextLayout = { textLayoutResult ->
                                            tabWidths[index] =
                                                with(density) { textLayoutResult.size.width.toDp() - 10.dp }
                                        })
                                },
                                modifier = Modifier
                                    .testTag(title.uppercase()),
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    if (index == 1 && viewModel.completedAppointmentsList.isEmpty()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.no_completed_appointments)
                                            )
                                        }
                                    } else coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalPager(
                        pageCount = if (viewModel.completedAppointmentsList.isEmpty()) 1 else viewModel.tabs.size,
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> UpcomingAppointments(navController, viewModel)
                            1 -> CompletedAppointments(viewModel)
                        }
                    }
                }
                if (viewModel.showAllSlotsBookedDialog) {
                    AllSlotsBookedDialog {
                        viewModel.showAllSlotsBookedDialog = false
                    }
                }
                if (viewModel.showCancelAppointmentDialog) {
                    viewModel.patient?.let { patient ->
                        CancelAppointmentDialog(
                            patient = patient,
                            dateAndTime = viewModel.selectedAppointment!!.slot.start.toAppointmentDate()
                        ) { cancel ->
                            if (cancel) {
                                viewModel.cancelAppointment {
                                    Timber.d("manseeyy appointment cancelled")
                                    viewModel.getAppointmentsList(viewModel.patient!!.id)
                                }
                            }
                            viewModel.showCancelAppointmentDialog = false
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                viewModel.patient?.let { patient ->
                    AppointmentsFab(
                        navController,
                        patient,
                        viewModel.isFabSelected,
                        viewModel.todaysAppointment,
                        viewModel.ifAlreadyWaiting
                    ) { queueFabClicked ->
                        if (queueFabClicked) {
                            if (viewModel.todaysAppointment != null) {
                                // change status of patient to arrived and navigate to queue screen
                                viewModel.updateStatusToArrived {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        navController.popBackStack()
                                        navController.previousBackStackEntry?.savedStateHandle?.set(
                                            NavControllerConstants.PATIENT_ARRIVED,
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
                                            navController.popBackStack()
                                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                                NavControllerConstants.ADD_TO_QUEUE,
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
        }
    )
}
