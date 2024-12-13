package com.latticeonfhir.android.ui.searchpatient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    viewModel: SearchPatientViewModel = viewModel()
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
        modifier = Modifier.fillMaxSize()
            .imePadding(),
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
                        navController.navigateUp()
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
                        name = viewModel.patientName.ifEmpty { null },
                        patientId = viewModel.patientId.ifEmpty { null },
                        minAge = viewModel.minAge.toInt(),
                        maxAge = viewModel.maxAge.toInt(),
                        addressLine1 = viewModel.address.addressLine1.ifEmpty { null },
                        addressLine2 = viewModel.address.addressLine2.ifEmpty { null },
                        postalCode = viewModel.address.pincode.ifEmpty { null },
                        state = viewModel.address.state.ifEmpty { null },
                        district = viewModel.address.district.ifEmpty { null },
                        city = viewModel.address.city.ifEmpty { null },
                        lastFacilityVisit = viewModel.visitSelected,
                        gender = viewModel.gender.ifEmpty { null }
                    )
                    if (viewModel.fromHouseholdMember) {
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
                    .padding(start = 30.dp),
                enabled = !(viewModel.isNameValid || viewModel.isPatientIdValid || viewModel.address.isPostalCodeValid)
            ) {
                Text(
                    text = stringResource(id = R.string.search),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
