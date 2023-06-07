package com.latticeonfhir.android.ui.searchpatient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPatient(
    navController: NavController,
    viewModel : SearchPatientViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                "fromHouseholdMember"
            ) == true
        ) {
            viewModel.fromHouseholdMember = true
            viewModel.patientFrom =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.fromHouseholdMember) stringResource(id = R.string.search_patients) else stringResource(
                            id = R.string.advanced_search
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "CLEAR_ICON"
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
                SearchPatientForm(searchPatientViewModel = viewModel)
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    val searchParameters = SearchParameters(
                        name = if (viewModel.patientName.isEmpty()) null else viewModel.patientName,
                        patientId = if (viewModel.patientId.isEmpty()) null else viewModel.patientId,
                        minAge = viewModel.minAge.toInt(),
                        maxAge = viewModel.maxAge.toInt(),
                        addressLine1 = if (viewModel.address.addressLine1.isEmpty()) null else viewModel.address.addressLine1,
                        addressLine2 = if (viewModel.address.addressLine2.isEmpty()) null else viewModel.address.addressLine2,
                        postalCode = if (viewModel.address.pincode.isEmpty()) null else viewModel.address.pincode,
                        state = if (viewModel.address.state.isEmpty()) null else viewModel.address.state,
                        district = if (viewModel.address.district.isEmpty()) null else viewModel.address.district,
                        city = if (viewModel.address.city.isEmpty()) null else viewModel.address.city,
                        lastFacilityVisit = viewModel.visitSelected,
                        gender = if (viewModel.gender.isEmpty()) null else viewModel.gender
                    )
                    if (viewModel.fromHouseholdMember){
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "searchParameters", searchParameters
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient", viewModel.patientFrom
                        )
                        navController.navigate(Screen.SearchResult.route)
                    } else {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "isSearchResult", true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "searchParameters", searchParameters
                        )
                        navController.navigate(Screen.LandingScreen.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp)
            ) {
                Text(text = stringResource(id = R.string.search), style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
