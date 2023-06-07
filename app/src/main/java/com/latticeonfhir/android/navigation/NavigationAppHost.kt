package com.latticeonfhir.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.latticeonfhir.android.LandingScreen
import com.latticeonfhir.android.ui.householdmember.connectpatient.ConnectPatient
import com.latticeonfhir.android.ui.householdmember.searchresult.SearchResult
import com.latticeonfhir.android.ui.login.OtpScreen
import com.latticeonfhir.android.ui.login.PhoneEmailScreen
import com.latticeonfhir.android.ui.main.patientlandingscreen.AddHouseholdMember
import com.latticeonfhir.android.ui.householdmember.HouseholdMembersScreen
import com.latticeonfhir.android.ui.patientlandingscreen.PatientLandingScreen
import com.latticeonfhir.android.ui.patientregistration.PatientRegistration
import com.latticeonfhir.android.ui.main.patientregistration.PatientRegistrationPreview
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.android.ui.prescription.PrescriptionScreen
import com.latticeonfhir.android.ui.prescription.filldetails.FillDetailsScreen
import com.latticeonfhir.android.ui.searchpatient.SearchPatient

@Composable
fun NavigationAppHost(navController: NavController, startDest: String) {
    NavHost(navController = navController as NavHostController, startDestination = startDest){
        composable(Screen.PhoneEmailScreen.route){ PhoneEmailScreen(navController) }
        composable(Screen.OtpScreen.route){ OtpScreen(navController) }
        composable(Screen.LandingScreen.route){ LandingScreen(navController = navController)}
        composable(Screen.SearchPatientScreen.route){ SearchPatient(navController = navController) }
        composable(Screen.PatientRegistrationScreen.route){ PatientRegistration(navController = navController) }
        composable(Screen.PatientRegistrationPreviewScreen.route){ PatientRegistrationPreview(navController = navController) }
        composable(Screen.PatientLandingScreen.route){ PatientLandingScreen(navController = navController) }
        composable(Screen.HouseholdMembersScreen.route){ HouseholdMembersScreen(navController = navController) }
        composable(Screen.AddHouseholdMember.route){ AddHouseholdMember(navController = navController) }
        composable(Screen.ConfirmRelationship.route){ ConfirmRelationship(navController = navController) }
        composable(Screen.SearchResult.route){ SearchResult(navController = navController) }
        composable(Screen.ConnectPatient.route){ ConnectPatient(navController = navController) }
        composable(Screen.Prescription.route){ PrescriptionScreen(navController = navController) }
        //composable(Screen.FillDetails.route){ FillDetailsScreen(navController = navController) }
    }
}