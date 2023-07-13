@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.latticeonfhir.android.ui.appointments

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.AppointmentsFab
import com.latticeonfhir.android.ui.common.customTabIndicatorOffset
import com.latticeonfhir.android.ui.theme.Neutral90
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppointmentsScreen(
    navController: NavController,
    viewModel: AppointmentsScreenViewModel = viewModel()
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

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
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
                        selectedTabIndex = viewModel.tabIndex,
                        modifier = Modifier.testTag("TABS"),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.customTabIndicatorOffset(
                                    currentTabPosition = tabPositions[viewModel.tabIndex],
                                    tabWidth = tabWidths[viewModel.tabIndex]
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
                                selected = viewModel.tabIndex == index,
                                onClick = {
                                    if (index == 1 && viewModel.completedAppointmentsList.isEmpty()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.no_completed_appointments)
                                            )
                                        }
                                    } else viewModel.tabIndex = index
                                },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    AnimatedContent(
                        targetState = viewModel.tabIndex,
                        transitionSpec = {
                            if (viewModel.tabIndex == 0) {
                                slideIntoContainer(
                                    animationSpec = tween(300, easing = EaseIn),
                                    towards = AnimatedContentScope.SlideDirection.Right
                                ).with(
                                    slideOutOfContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = AnimatedContentScope.SlideDirection.Right
                                    )
                                )
                            } else {
                                slideIntoContainer(
                                    animationSpec = tween(300, easing = EaseIn),
                                    towards = AnimatedContentScope.SlideDirection.Left
                                ).with(
                                    slideOutOfContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = AnimatedContentScope.SlideDirection.Left
                                    )
                                )
                            }
                        }
                    ) { targetState ->
                        when (targetState) {
                            0 -> UpcomingAppointments(navController, viewModel)
                            1 -> CompletedAppointments(viewModel)
                        }
                    }
                }
                if (viewModel.showCancelAppointmentDialog) {
                    viewModel.patient?.let { patient ->
                        CancelAppointmentDialog(
                            patient = patient,
                            dateAndTime = viewModel.selectedAppointment
                        ) {
                            viewModel.showCancelAppointmentDialog = it
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (viewModel.tabIndex == 0) {
                viewModel.patient?.let { patient ->
                    AppointmentsFab(
                        navController,
                        patient,
                        viewModel.isFabSelected
                    ) {
                        viewModel.isFabSelected = it
                    }
                }
            }
        }
    )
}
