package com.latticeonfhir.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.latticeonfhir.features.prescription.navigation.prescriptionNavGraph
import com.latticeonfhir.features.household.ui.connectpatient.ConnectPatient
import com.latticeonfhir.android.ui.householdmember.searchresult.SearchResult
import com.latticeonfhir.android.ui.labtestandmedicalrecord.photo.upload.PhotoUploadScreen
import com.latticeonfhir.android.ui.patienteditscreen.basicinfo.EditBasicInformation
import com.latticeonfhir.android.ui.patientlandingscreen.PatientLandingScreen
import com.latticeonfhir.android.ui.patientregistration.PatientRegistration
import com.latticeonfhir.android.ui.searchpatient.SearchPatient
import com.latticeonfhir.features.auth.navigation.authNavGraph
import com.latticeonfhir.core.auth.navigation.authRoute
import com.latticeonfhir.features.cvd.ui.CVDRiskAssessmentScreen
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.symptomsanddiagnosis.navigation.symptomsAndDiagnosisNavGraph
import com.latticeonfhir.core.ui.householdmember.HouseholdMembersScreen
import com.latticeonfhir.core.ui.householdmember.addhouseholdmember.AddHouseholdMember
import com.latticeonfhir.core.ui.labtestandmedicalrecord.photo.view.PhotoViewScreen
import com.latticeonfhir.core.ui.landingscreen.LandingScreen
import com.latticeonfhir.core.ui.patienteditscreen.address.EditPatientAddress
import com.latticeonfhir.core.ui.patienteditscreen.identification.EditIdentification
import com.latticeonfhir.core.ui.patientprofile.PatientProfile
import com.latticeonfhir.core.ui.patientregistration.preview.PatientRegistrationPreview
import com.latticeonfhir.core.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.core.ui.vitalsscreen.VitalsScreen
import com.latticeonfhir.core.ui.vitalsscreen.addvitals.AddVitalsScreen
import com.latticeonfhir.features.vaccination.navigation.vaccinationNavGraph
import com.latticeonfhir.features.appointment.navigation.appointmentNavGraph
import com.latticeonfhir.features.cvd.navigation.cvdNavGraph
import com.latticeonfhir.features.dispense.navigation.dispenseNavGraph
import com.latticeonfhir.features.household.navigation.householdNavGraph
import com.latticeonfhir.features.patient.navigation.patientNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NavigationAppHost(navController: NavController, startDest: String) {
    NavHost(navController = navController as NavHostController, startDestination = startDest) {
        authNavGraph(navController = navController)
        patientNavGraph(navController)
        householdNavGraph(navController)
        prescriptionNavGraph(navController)

        cvdNavGraph(navController)

        composable(Screen.VitalsScreen.route) { VitalsScreen(navController = navController) }
        composable(Screen.AddVitalsScreen.route) { AddVitalsScreen(navController = navController) }

        symptomsAndDiagnosisNavGraph(navController)

        composable(Screen.LabAndMedPhotoUploadScreen.route) { PhotoUploadScreen(navController = navController) }
        composable(Screen.LabAndMedRecordPhotoViewScreen.route) { PhotoViewScreen(navController = navController) }

        appointmentNavGraph(navController)
        dispenseNavGraph(navController)
        vaccinationNavGraph(navController)

    }
}