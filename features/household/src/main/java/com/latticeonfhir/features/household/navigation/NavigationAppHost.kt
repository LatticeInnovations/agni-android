package com.latticeonfhir.features.household.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.household.ui.HouseholdMembersScreen
import com.latticeonfhir.features.household.ui.addhouseholdmember.AddHouseholdMember
import com.latticeonfhir.features.household.ui.connectpatient.ConnectPatient
import com.latticeonfhir.features.household.ui.searchresult.SearchResult

fun NavGraphBuilder.householdNavGraph(
    navController: NavController
) {
    composable(Screen.HouseholdMembersScreen.route) { HouseholdMembersScreen(navController = navController) }
    composable(Screen.AddHouseholdMember.route) { AddHouseholdMember(navController = navController) }
    composable(Screen.ConnectPatient.route) { ConnectPatient(navController = navController) }
    composable(Screen.SearchResult.route) { SearchResult(navController = navController) }
}