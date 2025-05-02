package com.latticeonfhir.features.symptomsanddiagnosis.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.latticeonfhir.features.symptomsanddiagnosis.ui.SymptomsAndDiagnosisScreen
import com.latticeonfhir.features.symptomsanddiagnosis.ui.selectsymptoms.SelectSymptomScreen

fun NavGraphBuilder.symptomsAndDiagnosisNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.SymptomsAndDiagnosisScreen.route,
        route = symptomsAndDiagnosisRoute
    ) {
        composable(Screen.SymptomsAndDiagnosisScreen.route) {
            SymptomsAndDiagnosisScreen(
                navController
            )
        }
        composable(Screen.AddSymptomsScreen.route) { SelectSymptomScreen(navController) }
    }
}