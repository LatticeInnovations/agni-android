package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.latticeonfhir.core.R
import com.latticeonfhir.core.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.DisplaySyncStatus
import com.latticeonfhir.core.ui.common.Loader
import com.latticeonfhir.core.ui.main.MainActivity
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.latticeonfhir.core.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isToday
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.network.ConnectivityObserver
import timber.log.Timber
import java.util.Date

@Composable
fun MyPatientScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current as MainActivity
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CheckNetwork(viewModel, context)
        val patientsList: LazyPagingItems<PatientResponse> = if (viewModel.isSearchResult) {
            viewModel.searchResultList.collectAsLazyPagingItems()
        } else {
            viewModel.patientList.collectAsLazyPagingItems()
        }
        if (viewModel.isLoading || patientsList.loadState.refresh == LoadState.Loading) {
            Loader()
        } else {
            if (patientsList.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_records))
                }
            } else {
                PatientList(patientsList, viewModel, navController)
            }
        }
    }
}

@Composable
private fun PatientList(
    patientsList: LazyPagingItems<PatientResponse>,
    viewModel: LandingScreenViewModel,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.testTag("patients list")) {
        items(
            count = patientsList.itemCount,
            key = patientsList.itemKey(),
            contentType = patientsList.itemContentType(
            )
        ) { index ->
            val item = patientsList[index]
            if (item != null) {
                var lastVisited: Date? by remember {
                    mutableStateOf(null)
                }
                viewModel.getLastVisitedOfPatient(item.id) {
                    lastVisited = it
                }
                PatientItemCard(
                    navController, item, lastVisited, viewModel
                )
            }
        }
        when (patientsList.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item {
                    Loader()
                }
            }

            is LoadState.Error -> {
                Timber.d("LOADING ERROR")
            }
        }

        when (patientsList.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item {
                    Loader()
                }
            }

            is LoadState.Error -> {
                Timber.d("LOADING ERROR")
            }
        }
    }
}

@Composable
private fun CheckNetwork(viewModel: LandingScreenViewModel, context: MainActivity) {
    if (context.connectivityStatus.value == ConnectivityObserver.Status.Available) {
        if (viewModel.syncStatus == WorkerStatus.OFFLINE) viewModel.syncStatus =
            WorkerStatus.TODO
        if (viewModel.syncStatus != WorkerStatus.TODO) DisplaySyncStatus(
            viewModel.syncStatus,
            viewModel.syncIcon,
            viewModel.syncStatusMessage
        )
    } else {
        viewModel.apply {
            syncStatusMessage = SyncStatusMessageEnum.OFFLINE.message
            syncStatus = WorkerStatus.OFFLINE
            syncIcon = 0
        }
        if (viewModel.syncStatus != WorkerStatus.TODO) DisplaySyncStatus(
            viewModel.syncStatus,
            viewModel.syncIcon,
            viewModel.syncStatusMessage
        )
    }
}

@Composable
private fun PatientItemCard(
    navController: NavController,
    patient: PatientResponse,
    lastVisited: Date?,
    viewModel: LandingScreenViewModel
) {
    val subtitle = "${patient.gender[0].uppercase()}/${
        patient.birthDate.toTimeInMilli().toAge()
    } · PID ${patient.fhirId}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    patient
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    SELECTED_INDEX,
                    viewModel.selectedIndex
                )
                navController.navigate(Screen.PatientLandingScreen.route)
                viewModel.hideSyncStatus()
            }
            .padding(15.dp)
            .testTag("PATIENT"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(8f)) {
            Text(
                text = NameConverter.getFullName(
                    patient.firstName,
                    patient.middleName,
                    patient.lastName
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = if (lastVisited != null) stringResource(
                id = R.string.last_visited,
                if (isToday(lastVisited)) stringResource(R.string.today)
                else lastVisited.toSlotDate()
            ) else stringResource(
                id = R.string.registered
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}