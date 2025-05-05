package com.latticeonfhir.features.vaccination.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.vaccination.ui.VaccinationScreen
import com.latticeonfhir.features.vaccination.ui.add.AddVaccinationScreen
import com.latticeonfhir.features.vaccination.ui.error.VaccinationErrorScreen
import com.latticeonfhir.features.vaccination.ui.view.ViewVaccinationScreen

fun NavGraphBuilder.vaccinationNavGraph(
    navController: NavController
) {
    composable(Screen.VaccinationScreen.route) {
        VaccinationScreen(
            navController = navController
        )
    }
    composable(Screen.AddVaccinationScreen.route) {
        AddVaccinationScreen(
            navController = navController
        )
    }
    composable(Screen.ViewVaccinationScreen.route) {
        ViewVaccinationScreen(
            navController = navController
        )
    }
    composable(Screen.VaccinationErrorScreen.route) {
        VaccinationErrorScreen(
            navController = navController
        )
    }
}