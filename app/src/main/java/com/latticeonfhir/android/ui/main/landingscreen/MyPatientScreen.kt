package com.latticeonfhir.android.ui.main.landingscreen

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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.latticeonfhir.android.ui.main.common.Loader
import com.latticeonfhir.android.ui.main.common.PatientItemCard
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@Composable
fun MyPatientScreen(viewModel: LandingScreenViewModel= hiltViewModel()) {
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
        val patientsList = viewModel.patientList.collectAsLazyPagingItems()
        LazyColumn (modifier = Modifier.testTag("patients list")){
            items(patientsList){patient ->
                if (patient != null) {
                    val name = patient.firstName +
                            if (patient.middleName.isNullOrEmpty()) "" else {" " + patient.middleName} +
                            if (patient.lastName.isNullOrEmpty()) "" else {" " + patient.lastName}
                    val age = Period.between(
                        patient.birthDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate(),
                        LocalDate.now()
                    ).years
                    PatientItemCard(
                        name = name,
                        patientId = "${patient.gender[0].uppercase()}/$age · PID ${patient.fhirId}",
                        metaData = "Referred: 12 Jan"
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