package com.latticeonfhir.core.vaccination.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.latticeonfhir.core.vaccination.ui.VaccinationScreen
import com.latticeonfhir.core.vaccination.ui.add.AddVaccinationScreen
import com.latticeonfhir.core.vaccination.ui.error.VaccinationErrorScreen
import com.latticeonfhir.core.vaccination.ui.view.ViewVaccinationScreen

fun NavGraphBuilder.vaccinationNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.VaccinationScreen.route,
        route = vaccinationRoute
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
}