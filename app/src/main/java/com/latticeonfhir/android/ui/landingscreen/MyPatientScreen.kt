package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.common.PatientItemCard

@Composable
fun MyPatientScreen(navController: NavController, viewModel: LandingScreenViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            AssistChip(
                onClick = { /*TODO*/ },
                label = {
                    Text(
                        text = "Category 1",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.width(3.dp))
            AssistChip(
                onClick = { /*TODO*/ },
                label = {
                    Text(
                        text = "Category 2",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface)
                }
            )
            Spacer(modifier = Modifier.width(3.dp))
            AssistChip(
                onClick = { /*TODO*/ },
                label = {
                    Text(
                        text = "Category 3",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface)
                }
            )
        }
        val patientsList: LazyPagingItems<PatientResponse>
        if (viewModel.isSearchResult){
            patientsList = viewModel.searchResultList.collectAsLazyPagingItems()
        } else {
            patientsList = viewModel.patientList.collectAsLazyPagingItems()
        }
        LazyColumn (modifier = Modifier.testTag("patients list")){
            items(patientsList){patient ->
                if (patient != null) {
                    PatientItemCard(
                        navController, patient
                    )
                }
            }
            when(patientsList.loadState.append){
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

            when(patientsList.loadState.refresh){
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