package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.common.PatientItemCard
import java.util.Date

@Composable
fun MyPatientScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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