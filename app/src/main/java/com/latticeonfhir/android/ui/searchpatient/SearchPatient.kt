package com.latticeonfhir.android.ui.searchpatient

import android.util.Log
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
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.model.SearchParameters
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
                        navController.popBackStack()
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
                SearchPatientForm(searchPatientViewModel = searchPatientViewModel)
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    val searchParameters = SearchParameters(
                        name = if(searchPatientViewModel.patientName.isEmpty()) null else searchPatientViewModel.patientName,
                        patientId = if(searchPatientViewModel.patientId.isEmpty()) null else searchPatientViewModel.patientId,
                        minAge = searchPatientViewModel.minAge.toInt(),
                        maxAge = searchPatientViewModel.maxAge.toInt(),
                        gender = GenderEnum.MALE.value,
                        addressLine1 = if(searchPatientViewModel.address.addressLine1.isEmpty()) null else searchPatientViewModel.address.addressLine1,
                        addressLine2 = if(searchPatientViewModel.address.addressLine2.isEmpty()) null else searchPatientViewModel.address.addressLine2,
                        postalCode = if(searchPatientViewModel.address.pincode.isEmpty()) null else searchPatientViewModel.address.pincode,
                        state = if(searchPatientViewModel.address.state.isEmpty()) null else searchPatientViewModel.address.state,
                        district = if(searchPatientViewModel.address.district.isEmpty()) null else searchPatientViewModel.address.district,
                        city = if(searchPatientViewModel.address.city.isEmpty()) null else searchPatientViewModel.address.city,
                        lastFacilityVisit = searchPatientViewModel.visitSelected
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "isSearchResult", true
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "searchParameters", searchParameters
                    )
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
