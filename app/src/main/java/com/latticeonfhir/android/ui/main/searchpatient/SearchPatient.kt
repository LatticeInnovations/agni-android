package com.latticeonfhir.android.ui.main.searchpatient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPatient(
    navController: NavController,
    searchPatientViewModel: SearchPatientViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Advanced Search",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (searchPatientViewModel.step == 2) searchPatientViewModel.step = 1
                        else {
                            navController.popBackStack()
                            //navController.navigate(Screen.LandingScreen.route)
                        }
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.testTag("back icon")
                        )
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
//                if (searchPatientViewModel.step == 1)
                SearchPatientForm(searchPatientViewModel = searchPatientViewModel)
//                else if (searchPatientViewModel.step == 2)
//                    SearchPatientResult(searchPatientViewModel = searchPatientViewModel)
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "isSearchResult", true
                    )
//                    navController.currentBackStackEntry?.savedStateHandle?.set(
//                        "searchPatientList", searchResultList
//                    )
                    navController.navigate(Screen.LandingScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp)
            ) {
                Text(text = "Search", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
