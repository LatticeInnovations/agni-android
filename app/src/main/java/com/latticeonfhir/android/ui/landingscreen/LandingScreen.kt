package com.latticeonfhir.android.ui.landingscreen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.landingscreen.*
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    val dateScrollState = rememberLazyListState()

    BackHandler(enabled = true) {
        if (viewModel.isSearching) {
            viewModel.isSearching = false
        } else if (viewModel.isSearchResult) {
            viewModel.isSearchResult = false
            viewModel.populateList()
        } else {
            activity.finish()
        }
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
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
            }
            viewModel.populateList()
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
                if (viewModel.isSearchResult) {
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
                                text = viewModel.items[viewModel.selectedIndex],
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.testTag("SEARCH_TITLE_TEXT")
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
                                    viewModel.selectedDate = Date()
                                    viewModel.weekList = viewModel.selectedDate.to14DaysWeek()
                                },
                                enabled = viewModel.selectedDate.toSlotDate() != Date().toSlotDate()
                            ) {
                                Text(text = stringResource(id = R.string.reset))
                            }
                            IconButton(onClick = {
                                viewModel.isSearchingInQueue = true
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
                                text = viewModel.items[viewModel.selectedIndex],
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
                NavigationBar(
                    modifier = Modifier.testTag("BOTTOM_NAV_BAR")
                ) {
                    val icons =
                        listOf(
                            R.drawable.patient_list,
                            R.drawable.list_alt,
                            R.drawable.person_icon
                        )
                    viewModel.items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = icons[index]),
                                    contentDescription = item,
                                    modifier = Modifier
                                        .size(30.dp, 28.dp)
                                        .padding(vertical = 3.dp)
                                )
                            },
                            label = {
                                Text(
                                    item,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = viewModel.selectedIndex == index,
                            onClick = { viewModel.selectedIndex = index },
                            modifier = Modifier.testTag(item + " tab")
                        )
                    }
                }
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when (viewModel.selectedIndex) {
                        0 -> MyPatientScreen(navController)
                        1 -> QueueScreen(navController, viewModel, dateScrollState, coroutineScope)
                        2 -> ProfileScreen()
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
        Box(
            modifier =
            if (!viewModel.isSearching) Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { }

        ) {
            AnimatedVisibility(
                visible = viewModel.isSearching,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
                        .clickable { }
                        .testTag("SEARCH_LAYOUT"),
                    verticalArrangement = Arrangement.Top
                ) {
                    TextField(
                        value = viewModel.searchQuery,
                        onValueChange = {
                            viewModel.searchQuery = it
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                viewModel.searchQuery = ""
                                viewModel.isSearching = false
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                            }
                        },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                focusRequester.requestFocus()
                            }
                            .testTag("SEARCH_TEXT_FIELD"),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.isSearchResult = true
                                viewModel.isSearching = false
                                viewModel.isSearchingByQuery = true
                                viewModel.insertRecentSearch()
                                viewModel.populateList()
                            }
                        ),
                        singleLine = true
                    )
                    LazyColumn(modifier = Modifier.testTag("PREVIOUS_SEARCHES")) {
                        items(viewModel.previousSearchList) { listItem ->
                            PreviousSearches(
                                listItem, viewModel
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
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            AnimatedVisibility(
                visible = viewModel.isSearchingInQueue,
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
                    TextField(
                        value = viewModel.searchQueueQuery,
                        onValueChange = {
                            viewModel.searchQueueQuery = it
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                viewModel.searchQueueQuery = ""
                                viewModel.isSearchingInQueue = false
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                            }
                        },
                        trailingIcon = {
                            if (viewModel.searchQueueQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.searchQueueQuery = ""
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
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.isSearchingInQueue = false
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
                        .clickable { }
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
                    viewModel.statusList.forEach { status ->
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                        Divider(
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
            .padding(14.dp)
            .clickable {
                viewModel.isSearchResult = true
                viewModel.isSearching = false
                viewModel.isSearchingByQuery = true
                viewModel.searchQuery = listItem
                viewModel.populateList()
            },
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