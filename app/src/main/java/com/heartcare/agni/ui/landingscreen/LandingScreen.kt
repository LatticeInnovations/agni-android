package com.heartcare.agni.ui.landingscreen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum.Companion.fromLabel
import com.heartcare.agni.data.local.model.search.SearchParameters
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.BottomNavBar
import com.heartcare.agni.utils.constants.NavControllerConstants.ADD_TO_QUEUE
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT_ARRIVED
import com.heartcare.agni.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toYear
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel(),
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current as Activity
    val dateScrollState = rememberLazyListState()
    BackHandler(enabled = true) {
        when (viewModel.selectedIndex) {
            2 -> viewModel.selectedIndex = 0
            1 -> {
                if (queueViewModel.isSearchingInQueue || queueViewModel.searchQueueQuery.isNotBlank()) {
                    queueViewModel.isSearchingInQueue = false
                    queueViewModel.searchQueueQuery = ""
                    queueViewModel.getAppointmentListByDate()
                } else if (viewModel.showStatusChangeLayout) {
                    viewModel.showStatusChangeLayout = false
                } else viewModel.selectedIndex = 0
            }

            0 -> {
                if (viewModel.isSearching) {
                    viewModel.isSearching = false
                } else if (viewModel.isSearchResult) {
                    viewModel.isSearchResult = false
                    viewModel.populateList()
                } else {
                    activity.finish()
                }
            }
        }
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.selectedIndex =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>(
                    SELECTED_INDEX
                ) ?: 0
            if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    "isSearchResult"
                ) == true
            ) {
                viewModel.isSearchResult = true
                viewModel.searchParameters =
                    navController.previousBackStackEntry?.savedStateHandle?.get<SearchParameters>(
                        "searchParameters"
                    )
            } else if (
                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    "loggedIn"
                ) == true
            ) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Logged in successfully"
                    )
                }
            } else {
                viewModel.syncData()
            }
            viewModel.populateList()

            viewModel.addedToQueue =
                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    ADD_TO_QUEUE
                ) == true
            viewModel.patientArrived =
                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    PATIENT_ARRIVED
                ) == true
            if (viewModel.addedToQueue) {
                viewModel.selectedIndex = 1
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = activity.getString(R.string.added_to_queue)
                    )
                }
                navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(ADD_TO_QUEUE)
            }

            if (viewModel.patientArrived) {
                viewModel.selectedIndex = 1
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = activity.getString(R.string.status_updated_to_arrived)
                    )
                }
                navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                    PATIENT_ARRIVED
                )
            }
            navController.clearBackStack(Screen.LandingScreen.route)
        }
        viewModel.isLaunched = true
    }

    LaunchedEffect(viewModel.logoutUser) {
        if (viewModel.logoutUser) {
            viewModel.logout()
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "logoutUser",
                viewModel.logoutUser
            )
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "logoutReason",
                viewModel.logoutReason
            )
            navController.navigate(Screen.PhoneEmailScreen.route)
            viewModel.logoutUser = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (viewModel.isSearchResult && viewModel.selectedIndex == 0) {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (viewModel.isLoading) "Searching..." else "${viewModel.size} matches found",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.testTag("SEARCH_TITLE_TEXT")
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        actions = {
                            IconButton(onClick = {
                                viewModel.isSearching = false
                                navController.navigate(Screen.SearchPatientScreen.route)
                            }) {
                                Icon(
                                    Icons.Default.Search, contentDescription = "SEARCH_ICON"
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.isSearchResult = false
                                viewModel.populateList()
                            }) {
                                Icon(
                                    Icons.Default.Clear, contentDescription = "CLEAR_ICON"
                                )
                            }
                        }
                    )
                } else if (viewModel.selectedIndex == 1) {
                    TopAppBar(
                        title = {
                            Text(
                                text = viewModel.headings[viewModel.selectedIndex],
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.testTag("HEADING_TAG")
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        actions = {
                            FilledTonalButton(
                                onClick = {
                                    coroutineScope.launch {
                                        dateScrollState.animateScrollToItem(7, scrollOffset = -130)
                                    }
                                    queueViewModel.selectedDate = Date()
                                    queueViewModel.weekList =
                                        queueViewModel.selectedDate.to14DaysWeek()
                                    queueViewModel.getAppointmentListByDate()
                                },
                                enabled = queueViewModel.selectedDate.toSlotDate() != Date().toSlotDate() || queueViewModel.selectedDate.toYear() != Date().toYear(),
                                modifier = Modifier.testTag("RESET_BTN")
                            ) {
                                Text(text = stringResource(id = R.string.today))
                            }
                            IconButton(onClick = {
                                queueViewModel.isSearchingInQueue = true
                            }) {
                                Icon(
                                    Icons.Default.Search, contentDescription = "SEARCH_ICON"
                                )
                            }
                        }
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = viewModel.headings[viewModel.selectedIndex],
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        actions = {
                            if (viewModel.selectedIndex == 0)
                                IconButton(onClick = {
                                    viewModel.searchQuery = ""
                                    viewModel.isSearching = true
                                    viewModel.getPreviousSearches()
                                }) {
                                    Icon(
                                        Icons.Default.Search, contentDescription = "SEARCH_ICON"
                                    )
                                }
                            else if (viewModel.selectedIndex == 2) {
                                IconButton(onClick = {
                                    viewModel.isLoggingOut = true
                                }) {
                                    Icon(
                                        painterResource(id = R.drawable.logout),
                                        contentDescription = "LOG_OUT_ICON",
                                        Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                    )
                }

            },
            floatingActionButton = {
                if (viewModel.selectedIndex == 0) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.hideSyncStatus()
                            navController.navigate(Screen.PatientRegistrationScreen.route)
                        },
                        content = {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.person_add),
                                    contentDescription = "ADD_PATIENT_ICON",
                                    modifier = Modifier
                                        .size(25.dp, 23.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Add Patient",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("ADD_PATIENT_TEXT")
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp)
                    )
                }
            },
            bottomBar = {
                BottomNavBar(
                    selectedIndex = viewModel.selectedIndex,
                    updateIndex = { index ->
                        if (index != 0) viewModel.hideSyncStatus()
                        viewModel.selectedIndex = index
                    }
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when (viewModel.selectedIndex) {
                        0 -> MyPatientScreen(navController)
                        1 -> QueueScreen(
                            navController,
                            viewModel,
                            dateScrollState,
                            coroutineScope,
                            snackbarHostState
                        )

                        2 -> ProfileScreen(navController, snackbarHostState)
                    }
                }
                if (viewModel.isLoggingOut) {
                    AlertDialog(
                        onDismissRequest = { viewModel.isLoggingOut = false },
                        title = {
                            Text(
                                text = stringResource(id = R.string.logout_dialog_title),
                                modifier = Modifier.testTag("DIALOG_TITLE")
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.logout_dialog_description),
                                modifier = Modifier.testTag("DIALOG_DESCRIPTION")
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.isLoggingOut = false
                                    viewModel.logout()
                                    navController.navigate(Screen.PhoneEmailScreen.route) {
                                        popUpTo(Screen.LandingScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("POSITIVE_BTN")
                            ) {
                                Text(
                                    stringResource(id = R.string.logout_btn)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    viewModel.isLoggingOut = false
                                },
                                modifier = Modifier.testTag("NEGATIVE_BTN")
                            ) {
                                Text(
                                    stringResource(id = R.string.no_go_back)
                                )
                            }
                        }
                    )
                }
            }
        )
        SearchView(viewModel, focusRequester, navController)
        val keyboardController = LocalSoftwareKeyboardController.current
        Box(
            modifier = Modifier.matchParentSize()
                .statusBarsPadding()
        ) {
            AnimatedVisibility(
                visible = queueViewModel.isSearchingInQueue && viewModel.selectedIndex == 1,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
                        .clickable { }
                        .testTag("QUEUE_SEARCH_LAYOUT"),
                    verticalArrangement = Arrangement.Top
                ) {
                    val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    TextField(
                        value = queueViewModel.searchQueueQuery,
                        onValueChange = {
                            queueViewModel.searchQueueQuery = it
                            queueViewModel.getAppointmentListByDate()
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                queueViewModel.searchQueueQuery = ""
                                queueViewModel.isSearchingInQueue = false
                                queueViewModel.getAppointmentListByDate()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "BACK_ICON"
                                )
                            }
                        },
                        trailingIcon = {
                            if (queueViewModel.searchQueueQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    queueViewModel.searchQueueQuery = ""
                                    queueViewModel.getAppointmentListByDate()
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                focusRequester.requestFocus()
                            }
                            .testTag("SEARCH_TEXT_FIELD"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true
                    )
                }
            }
        }
        Box(
            modifier =
            if (!viewModel.showStatusChangeLayout) Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = viewModel.showStatusChangeLayout,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .testTag("CHANGE_STATUS_LAYOUT"),
                    verticalArrangement = Arrangement.Top
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.status),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { viewModel.showStatusChangeLayout = false }) {
                                Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                            }
                        }
                    }
                    queueViewModel.statusList.forEach { status ->
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .clickable {
                                    queueViewModel.updateAppointmentStatus(fromLabel(status).value) {
                                        viewModel.showStatusChangeLayout = false
                                        queueViewModel.getAppointmentListByDate()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = if (status == "Arrived") activity.getString(
                                                    R.string.status_to_arrived
                                                )
                                                else activity.getString(R.string.status_to_completed)
                                            )
                                        }
                                    }
                                }
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun PreviousSearches(listItem: String, viewModel: LandingScreenViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.isSearchResult = true
                viewModel.isSearching = false
                viewModel.isSearchingByQuery = true
                viewModel.searchQuery = listItem
                viewModel.populateList()
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = R.drawable.search_history),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = listItem,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SearchView(
    viewModel: LandingScreenViewModel,
    focusRequester: FocusRequester,
    navController: NavController
) {
    Box(
        modifier = if (!viewModel.isSearching) Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        else Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { }

    ) {
        AnimatedVisibility(
            visible = viewModel.isSearching,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            Scaffold(
                modifier = Modifier.wrapContentHeight(),
                containerColor = Color.Transparent,
                topBar = {
                    SearchViewTopAppBar(viewModel, focusRequester)
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
                        .clickable { }
                        .testTag("SEARCH_LAYOUT"),
                    verticalArrangement = Arrangement.Top
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    LazyColumn(modifier = Modifier.testTag("PREVIOUS_SEARCHES")) {
                        items(viewModel.previousSearchList) { listItem ->
                            PreviousSearches(
                                listItem,
                                viewModel
                            )
                        }
                    }
                    if (viewModel.previousSearchList.isEmpty()) Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 14.dp, start = 14.dp, end = 14.dp),
                        onClick = {
                            viewModel.isSearching = false
                            navController.navigate(Screen.SearchPatientScreen.route)
                        }
                    ) {
                        Text(text = "Advanced search")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchViewTopAppBar(
    viewModel: LandingScreenViewModel,
    focusRequester: FocusRequester
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        ),
        navigationIcon = {
            IconButton(onClick = {
                viewModel.searchQuery = ""
                viewModel.isSearching = false
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "BACK_ICON"
                )
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .offset(x = (-16).dp)
                    .clickable(
                        enabled = true
                    ) { }
            ) {
                TextField(
                    value = viewModel.searchQuery,
                    onValueChange = {
                        viewModel.searchQuery = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        }
                        .testTag("SEARCH_TEXT_FIELD"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (viewModel.searchQuery.trim().isNotBlank()) {
                                viewModel.isSearchResult = true
                                viewModel.isSearchingByQuery = true
                                viewModel.insertRecentSearch()
                                viewModel.populateList()
                            }
                            viewModel.isSearching = false
                        }
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        },
        actions = {
            if (viewModel.searchQuery.isNotBlank()) {
                IconButton(onClick = {
                    viewModel.searchQuery = ""
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                }
            }
        }
    )
}
