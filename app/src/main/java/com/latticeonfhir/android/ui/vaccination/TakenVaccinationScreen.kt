package com.latticeonfhir.android.ui.vaccination

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.vaccination.VaccinationViewModel.Companion.TAKEN

@Composable
fun TakenVaccinationScreen(
    navController: NavController,
    viewModel: VaccinationViewModel
) {
    viewModel.patient?.let { patient ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (isSystemInDarkTheme()) Color.Black else Color.White
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AgeComposable(viewModel)
            if (viewModel.takenVaccinesList.isEmpty()) {
                VaccineEmptyScreen(
                    stringResource(R.string.no_vaccines_recorded),
                    stringResource(R.string.vaccine_log_info)
                )
            } else {
                Text(
                    text = "February 2024",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                VaccineCard(TAKEN, navController, patient)
                Text(
                    text = "January 2024",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                VaccineCard(TAKEN, navController, patient)
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}