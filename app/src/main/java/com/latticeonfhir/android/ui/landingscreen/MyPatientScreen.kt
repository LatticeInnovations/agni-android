package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.common.PatientItemCard
import com.latticeonfhir.android.ui.main.MainActivity
import com.latticeonfhir.android.ui.theme.Primary10
import com.latticeonfhir.android.ui.theme.SyncFailedColor
import com.latticeonfhir.android.utils.network.ConnectivityObserver
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
                        navController, item, lastVisited
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
                    // TODO
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
                    // TODO
                }
            }
        }
    }
}


@Composable
private fun DisplaySyncStatus(viewModel: LandingScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(23.dp)
            .background(
                color = when (viewModel.syncStatus) {
                    WorkerStatus.IN_PROGRESS -> {
                        MaterialTheme.colorScheme.surfaceVariant
                    }

                    WorkerStatus.FAILED -> {
                        SyncFailedColor
                    }

                    WorkerStatus.SUCCESS -> {
                        MaterialTheme.colorScheme.primaryContainer
                    }

                    WorkerStatus.OFFLINE -> {
                        MaterialTheme.colorScheme.outlineVariant
                    }

                    else -> {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.syncIcon != 0) Icon(
                painter = painterResource(id = viewModel.syncIcon),
                contentDescription = "",
                tint = setTextAndIconColor(viewModel = viewModel)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = viewModel.syncStatusMessage,
                color = setTextAndIconColor(viewModel),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CheckNetwork(viewModel: LandingScreenViewModel, context: MainActivity) {
    if (context.connectivityStatus.value == ConnectivityObserver.Status.Available) {
        if (viewModel.syncStatus == WorkerStatus.OFFLINE) viewModel.syncStatus =
            WorkerStatus.TODO
        if (viewModel.syncStatus != WorkerStatus.TODO) DisplaySyncStatus(viewModel)
    } else {
        viewModel.apply {
            syncStatusMessage = SyncStatusMessageEnum.OFFLINE.message
            syncStatus = WorkerStatus.OFFLINE
            syncIcon = 0
        }
        if (viewModel.syncStatus != WorkerStatus.TODO) DisplaySyncStatus(viewModel)
    }
}


@Composable
private fun setTextAndIconColor(viewModel: LandingScreenViewModel): Color {
    return when (viewModel.syncStatus) {
        WorkerStatus.FAILED -> {
            Primary10
        }

        else -> {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}