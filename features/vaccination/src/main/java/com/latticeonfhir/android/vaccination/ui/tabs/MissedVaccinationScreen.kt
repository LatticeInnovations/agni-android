package com.latticeonfhir.android.vaccination.ui.tabs

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.vaccination.R
import com.latticeonfhir.android.vaccination.ui.AgeComposable
import com.latticeonfhir.android.vaccination.ui.VaccinationViewModel
import com.latticeonfhir.android.vaccination.ui.VaccinationViewModel.Companion.MISSED
import com.latticeonfhir.android.vaccination.ui.VaccineCard
import com.latticeonfhir.android.vaccination.ui.VaccineEmptyScreen
import com.latticeonfhir.android.vaccination.ui.navigateToAddVaccine
import kotlinx.coroutines.launch

@Composable
fun MissedVaccinationScreen(
    navController: NavController,
    viewModel: VaccinationViewModel
) {
    val coroutineScope = rememberCoroutineScope()
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
            if (viewModel.missedImmunizationRecommendationList.isEmpty()) {
                VaccineEmptyScreen(
                    stringResource(R.string.no_missed_vaccines),
                    stringResource(R.string.all_vaccines_on_schedule)
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.overdue_count,
                        viewModel.missedImmunizationRecommendationList.count()
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                viewModel.missedImmunizationRecommendationList.forEach { vaccine ->
                    VaccineCard(
                        missedOrTaken = MISSED,
                        vaccine = vaccine,
                        onClick = {
                            viewModel.getAppointmentInfo {
                                if (viewModel.canAddVaccination) {
                                    // navigate to add vaccination screen
                                    coroutineScope.launch {
                                        navigateToAddVaccine(
                                            navController,
                                            vaccine,
                                            patient,
                                            viewModel.immunizationRecommendationList
                                        )
                                    }
                                } else if (viewModel.isAppointmentCompleted) {
                                    viewModel.showAppointmentCompletedDialog = true
                                } else {
                                    viewModel.selectedVaccine = vaccine
                                    viewModel.showAddToQueueDialog = true
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}
