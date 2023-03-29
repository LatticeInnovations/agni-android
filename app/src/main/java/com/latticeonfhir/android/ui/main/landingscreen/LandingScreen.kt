package com.latticeonfhir.android

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.main.landingscreen.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isSearching") == true) {
        viewModel.isSearching = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (viewModel.isSearching) {
                Surface(
                    modifier = Modifier.height(64.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        TextField(
                            value = viewModel.searchQuery,
                            onValueChange = {
                                viewModel.searchQuery = it
                            },
                            leadingIcon = {
                                IconButton(onClick = {
                                    viewModel.isSearching = false
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "")
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "")
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
                            )
                        )
                    }
                }
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
                                viewModel.isSearching = true
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
            if (!viewModel.isSearching && viewModel.selectedIndex == 0) {
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
            if (!viewModel.isSearching) {
                NavigationBar {
                    val icons =
                        listOf(R.drawable.patient_list, R.drawable.list_alt, R.drawable.person_icon)
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
                            label = { Text(item, style = MaterialTheme.typography.labelMedium) },
                            selected = viewModel.selectedIndex == index,
                            onClick = { viewModel.selectedIndex = index }
                        )
                    }
                }
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (viewModel.isSearching)
                    SearchScreen(navController = navController)
                else {
                    when (viewModel.selectedIndex) {
                        0 -> MyPatientScreen()
                        1 -> QueueScreen()
                        2 -> ProfileScreen()
                    }
                }
            }
        }
    )
}