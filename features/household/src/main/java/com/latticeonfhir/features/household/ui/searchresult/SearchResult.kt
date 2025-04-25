package com.latticeonfhir.features.household.ui.searchresult

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.latticeonfhir.android.ui.Loader
import com.latticeonfhir.core.data.local.model.search.SearchParameters
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.utils.converters.TimeConverter.toAge
import com.latticeonfhir.core.utils.converters.TimeConverter.toTimeInMilli
import com.latticeonfhir.core.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.features.household.R
import com.latticeonfhir.utils.converters.responseconverter.AddressConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResult(navController: NavController, viewModel: SearchResultViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patientFrom =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
            viewModel.searchParameters =
                navController.previousBackStackEntry?.savedStateHandle?.get<SearchParameters>(
                    "searchParameters"
                )
        }
        viewModel.searchPatient(viewModel.searchParameters!!)
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.search_results, viewModel.size))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                val patientsList = viewModel.searchResultList.collectAsLazyPagingItems()
                LazyColumn(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = if (viewModel.selectedMembersList.isNotEmpty()) 60.dp else 20.dp
                    )
                ) {
                    items(
                        count = patientsList.itemCount,
                        key = patientsList.itemKey(),
                        contentType = patientsList.itemContentType(
                        )
                    ) { index ->
                        SearchResultRow(patientsList[index]!!, viewModel)
                    }
                    when (patientsList.loadState.append) {
                        is LoadState.NotLoading -> Unit
                        LoadState.Loading -> {
                            item {
                                Loader()
                            }
                        }

                        is LoadState.Error -> Unit
                    }

                    when (patientsList.loadState.refresh) {
                        is LoadState.NotLoading -> Unit
                        LoadState.Loading -> {
                            item {
                                Loader()
                            }
                        }

                        is LoadState.Error -> Unit
                    }
                }
            }
        },
        floatingActionButton = {
            if (viewModel.selectedMembersList.isNotEmpty()) {
                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patientFrom",
                            viewModel.patientFrom
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "selectedMembersList",
                            viewModel.selectedMembersList.toMutableList()
                        )
                        navController.navigate(Screen.ConnectPatient.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.connect),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}

@Composable
fun SearchResultRow(patient: PatientResponse, viewModel: SearchResultViewModel) {
    val checkedState = remember { mutableStateOf(viewModel.selectedMembersList.contains(patient)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                checkedState.value = !checkedState.value
                if (checkedState.value) {
                    viewModel.selectedMembersList.add(patient)
                } else {
                    viewModel.selectedMembersList.remove(patient)
                }
            },
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                if (it) {
                    viewModel.selectedMembersList.add(patient)
                } else {
                    viewModel.selectedMembersList.remove(patient)
                }
            },
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = NameConverter.getFullName(
                    patient.firstName,
                    patient.middleName,
                    patient.lastName
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${patient.gender[0].uppercase()}/${
                    patient.birthDate.toTimeInMilli().toAge()
                } · PID ${patient.fhirId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = AddressConverter.getAddress(patient.permanentAddress),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}