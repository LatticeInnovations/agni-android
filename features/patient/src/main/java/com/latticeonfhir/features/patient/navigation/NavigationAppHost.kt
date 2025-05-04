package com.latticeonfhir.features.patient.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.patient.ui.landingscreen.LandingScreen
import com.latticeonfhir.features.patient.ui.patienteditscreen.address.EditPatientAddress
import com.latticeonfhir.features.patient.ui.patienteditscreen.basicinfo.EditBasicInformation
import com.latticeonfhir.features.patient.ui.patientlandingscreen.PatientLandingScreen
import com.latticeonfhir.features.patient.ui.patientregistration.PatientRegistration
import com.latticeonfhir.features.patient.ui.patientregistration.preview.PatientRegistrationPreview
import com.latticeonfhir.features.patient.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.features.patient.ui.searchpatient.SearchPatient
import com.latticeonfhir.features.patient.ui.patientprofile.PatientProfile
import com.latticeonfhir.features.patient.ui.patienteditscreen.identification.EditIdentification

fun NavGraphBuilder.patientNavGraph(
    navController: NavController
) {
    composable(Screen.LandingScreen.route) { LandingScreen(navController = navController) }
    composable(Screen.SearchPatientScreen.route) { SearchPatient(navController = navController) }
    composable(Screen.PatientRegistrationScreen.route) { PatientRegistration(navController = navController) }
    composable(Screen.PatientRegistrationPreviewScreen.route) { PatientRegistrationPreview(navController = navController) }
    composable(Screen.PatientLandingScreen.route) { PatientLandingScreen(navController = navController) }
    composable(Screen.ConfirmRelationship.route) { ConfirmRelationship(navController = navController) }
    composable(Screen.PatientProfile.route) { PatientProfile(navController = navController) }
    composable(Screen.EditBasicInfo.route) { EditBasicInformation(navController = navController) }
    composable(Screen.EditIdentification.route) { EditIdentification(navController = navController) }
    composable(Screen.EditAddress.route) { EditPatientAddress(navController = navController) }
}