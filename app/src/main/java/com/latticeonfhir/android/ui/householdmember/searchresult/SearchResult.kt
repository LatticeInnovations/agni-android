package com.latticeonfhir.android.ui.householdmember.searchresult

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
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_FROM
import com.latticeonfhir.android.utils.constants.NavControllerConstants.SEARCH_PARAMETERS
import com.latticeonfhir.android.utils.constants.NavControllerConstants.SELECTED_MEMBERS_LIST
import com.latticeonfhir.android.utils.converters.responseconverter.AddressConverter.getAddressFhir
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getLatticeId
import org.hl7.fhir.r4.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResult(navController: NavController, viewModel: SearchResultViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patientFrom =
                navController.previousBackStackEntry?.savedStateHandle?.get<Patient>(
                    PATIENT
                )!!
            viewModel.searchParameters =
                navController.previousBackStackEntry?.savedStateHandle?.get<SearchParameters>(
                    SEARCH_PARAMETERS
                )
            viewModel.searchPatient(viewModel.searchParameters!!)
            viewModel.isLaunched = true
        }
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
                            PATIENT_FROM,
                            viewModel.patientFrom
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            SELECTED_MEMBERS_LIST,
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
fun SearchResultRow(patient: Patient, viewModel: SearchResultViewModel) {
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
                text = patient.nameFirstRep.nameAsSingleString,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${patient.gender.display[0].uppercase()}/${
                    patient.birthDate.time.toAge()
                } · PID ${getLatticeId(patient)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = getAddressFhir(patient.addressFirstRep),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}