package com.latticeonfhir.features.symptomsanddiagnosis.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.symptomsanddiagnosis.ui.SymptomsAndDiagnosisScreen
import com.latticeonfhir.features.symptomsanddiagnosis.ui.selectsymptoms.SelectSymptomScreen

fun NavGraphBuilder.symptomsAndDiagnosisNavGraph(
    navController: NavController
) {
    composable(Screen.SymptomsAndDiagnosisScreen.route) {
        SymptomsAndDiagnosisScreen(
            navController
        )
    }
    composable(Screen.AddSymptomsScreen.route) { SelectSymptomScreen(navController) }
}