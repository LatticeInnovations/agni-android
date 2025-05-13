package com.latticeonfhir.features.vitals.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.vitals.VitalsScreen
import com.latticeonfhir.features.vitals.addvitals.AddVitalsScreen

fun NavGraphBuilder.vitalsNavGraph(
    navController: NavController
) {
    composable(Screen.VitalsScreen.route) {
        VitalsScreen(
            navController = navController
        )
    }
}

fun NavGraphBuilder.addVitalsNavGraph(
    navController: NavController
) {
    composable(Screen.AddVitalsScreen.route) {
        AddVitalsScreen(
            navController = navController
        )
    }
}