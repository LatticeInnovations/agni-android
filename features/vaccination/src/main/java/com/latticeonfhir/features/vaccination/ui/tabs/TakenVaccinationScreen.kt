package com.latticeonfhir.features.vaccination.ui.tabs

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
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE
import com.latticeonfhir.core.utils.converters.TimeConverter.toMonthAndYear
import com.latticeonfhir.features.vaccination.R
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.vaccination.ui.AgeComposable
import com.latticeonfhir.features.vaccination.ui.VaccinationViewModel
import com.latticeonfhir.features.vaccination.ui.VaccinationViewModel.Companion.TAKEN
import com.latticeonfhir.features.vaccination.ui.VaccineCard
import com.latticeonfhir.features.vaccination.ui.VaccineEmptyScreen

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
            if (viewModel.takenImmunizationRecommendationList.isEmpty()) {
                VaccineEmptyScreen(
                    stringResource(R.string.no_vaccines_recorded),
                    stringResource(R.string.vaccine_log_info)
                )
            } else {
                viewModel.takenImmunizationRecommendationList.groupBy {
                    it.takenOn!!.toMonthAndYear()
                }.forEach { (takenOn, listOfVaccine) ->
                    Text(
                        text = takenOn,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    listOfVaccine.forEach { vaccine ->
                        VaccineCard(
                            missedOrTaken = TAKEN,
                            vaccine = vaccine,
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    patient
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    VACCINE,
                                    vaccine
                                )
                                navController.navigate(Screen.ViewVaccinationScreen.route)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}