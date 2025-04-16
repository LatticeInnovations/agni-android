package com.latticeonfhir.features.dispense.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.latticeonfhir.features.dispense.ui.DrugDispenseScreen
import com.latticeonfhir.features.dispense.ui.otc.OTCScreen
import com.latticeonfhir.features.dispense.ui.prescription.dispenseprescription.DispensePrescriptionScreen

fun NavGraphBuilder.dispenseNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.DrugDispenseScreen.route,
        route = dispenseRoute
    ) {
        composable(Screen.DrugDispenseScreen.route) {
            DrugDispenseScreen(
                navController = navController
            )
        }
        composable(Screen.DispensePrescriptionScreen.route) {
            DispensePrescriptionScreen(
                navController = navController
            )
        }
        composable(Screen.OTCScreen.route) { OTCScreen(navController = navController) }
    }
}