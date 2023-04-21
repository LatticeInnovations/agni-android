package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Right
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Up
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.constants.Constants
import com.latticeonfhir.android.ui.householdmember.HouseholdMemberViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HouseholdMembersScreen(
    navController: NavController,
    viewModel: HouseholdMemberViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                "patient"
            )
        }
        viewModel.isLaunched = true
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
                    }
                },
                title = {
                    val subTitle = "${viewModel.patient?.gender?.get(0)?.uppercase()}/${viewModel.patient?.birthDate?.let {
                        Constants.GetAge(
                            it
                        )
                    }}"
                    Column {
                        Text(
                            text = "Household members",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(text = "${Constants.GetFullName(viewModel.patient?.firstName, viewModel.patient?.middleName, viewModel.patient?.lastName)}, $subTitle", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            Box(modifier = Modifier.padding(it)) {

                val tabIndicatorHeight = 2.dp
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = viewModel.tabIndex
                    ) {
                        viewModel.tabs.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title) },
                                selected = viewModel.tabIndex == index,
                                onClick = { viewModel.tabIndex = index },
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
                                    towards = Right
                                ).with(
                                    slideOutOfContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = Right
                                    )
                                )
                            } else {
                                slideIntoContainer(
                                    animationSpec = tween(300, easing = EaseIn),
                                    towards = Left
                                ).with(
                                    slideOutOfContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = Left
                                    )
                                )
                            }
                        }
                    ) { targetState ->
                        when (targetState) {
                            0 -> viewModel.patient?.let { it1 -> MembersScreen(it1) }
                            1 -> viewModel.patient?.let { it1 -> SuggestionsScreen(it1, snackbarHostState, scope) }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (viewModel.tabIndex == 0) {
                if (!viewModel.isUpdateSelected) {
                    FloatingActionButton(
                        onClick = { viewModel.isUpdateSelected = true },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.group_icon),
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Update")
                        }
                    }
                } else {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Column {
                            FloatingActionButton(
                                onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "patient",
                                        viewModel.patient
                                    )
                                    navController.navigate(Screen.AddHouseholdMember.route)
                                    viewModel.isUpdateSelected = false
                                },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Add member")
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            FloatingActionButton(
                                onClick = { },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Edit existing")
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_icon),
                                        contentDescription = null,
                                        Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            FloatingActionButton(
                                onClick = { viewModel.isUpdateSelected = false },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}