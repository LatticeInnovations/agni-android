package com.latticeonfhir.features.cvd.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.cvd.ui.CVDRiskAssessmentScreen

fun NavGraphBuilder.cvdNavGraph(
    navController: NavController
) {
    composable(Screen.CVDRiskAssessmentScreen.route) {
        CVDRiskAssessmentScreen(
            navController
        )
    }
}