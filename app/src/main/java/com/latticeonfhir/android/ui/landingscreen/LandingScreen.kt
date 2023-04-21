package com.latticeonfhir.android

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.PatientItemCard
import com.latticeonfhir.android.ui.landingscreen.LandingScreenViewModel
import com.latticeonfhir.android.ui.landingscreen.MyPatientScreen
import com.latticeonfhir.android.ui.landingscreen.ProfileScreen
import com.latticeonfhir.android.ui.landingscreen.QueueScreen
import com.latticeonfhir.android.ui.landingscreen.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }

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
            }

            viewModel.populateList()
            viewModel.isLaunched = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (viewModel.isSearchResult) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "${viewModel.size} matches found",
                                style = MaterialTheme.typography.titleLarge
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
                                    Icons.Default.Search, contentDescription = null
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.isSearchResult = false
                                viewModel.populateList()
                                navController.popBackStack(Screen.LandingScreen.route, false)
                            }) {
                                Icon(
                                    Icons.Default.Clear, contentDescription = null,
                                    modifier = Modifier.testTag("clear icon")
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
                                        Icons.Default.Search, contentDescription = null,
                                        modifier = Modifier.testTag("search icon")
                                    )
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
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp, 23.dp)
                                        .testTag("add patient icon"),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Add Patient",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("add patient text")
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp)
                    )
                }
            },
            bottomBar = {
                NavigationBar {
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
                            onClick = { viewModel.selectedIndex = index }
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
                        1 -> QueueScreen()
                        2 -> ProfileScreen()
                    }
                }
            }
        )
        Box(
            modifier =
            Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = if (viewModel.isSearching) 0.5f else 0f))

        ) {
            AnimatedVisibility(
                visible = viewModel.isSearching,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)),
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
                                Icon(Icons.Default.ArrowBack, contentDescription = "")
                            }
                        },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                focusRequester.requestFocus()
                            },
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
                    LazyColumn(modifier = Modifier.testTag("patients list")) {
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