package com.latticeonfhir.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.latticeonfhir.android.LandingScreen
import com.latticeonfhir.android.ui.main.patientlandingscreen.AddHouseholdMember
import com.latticeonfhir.android.ui.main.patientlandingscreen.HouseholdMembersScreen
import com.latticeonfhir.android.ui.main.patientlandingscreen.PatientLandingScreen
import com.latticeonfhir.android.ui.patientregistration.PatientRegistration
import com.latticeonfhir.android.ui.main.patientregistration.PatientRegistrationPreview
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.android.ui.searchpatient.SearchPatient

@Composable
fun NavigationAppHost(navController: NavController) {
    NavHost(navController = navController as NavHostController, startDestination = "landing_screen"){
        composable(Screen.LandingScreen.route){ LandingScreen(navController = navController)}
        composable(Screen.SearchPatientScreen.route){ SearchPatient(navController = navController) }
        composable(Screen.PatientRegistrationScreen.route){ PatientRegistration(navController = navController) }
        composable(Screen.PatientRegistrationPreviewScreen.route){ PatientRegistrationPreview(navController = navController) }
        composable(Screen.PatientLandingScreen.route){ PatientLandingScreen(navController = navController) }
        composable(Screen.HouseholdMembersScreen.route){ HouseholdMembersScreen(navController = navController) }
        composable(Screen.AddHouseholdMember.route){ AddHouseholdMember(navController = navController) }
        composable(Screen.ConfirmRelationship.route){ ConfirmRelationship(navController = navController) }
    }
}