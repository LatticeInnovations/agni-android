package com.latticeonfhir.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.latticeonfhir.android.auth.navigation.authNavGraph
import com.latticeonfhir.android.auth.navigation.authRoute
import com.latticeonfhir.android.cvd.ui.CVDRiskAssessmentScreen
import com.latticeonfhir.android.symptomsanddiagnosis.navigation.symptomsAndDiagnosisNavGraph
import com.latticeonfhir.android.ui.appointments.AppointmentsScreen
import com.latticeonfhir.android.ui.appointments.schedule.ScheduleAppointments
import com.latticeonfhir.android.ui.dispense.DrugDispenseScreen
import com.latticeonfhir.android.ui.dispense.otc.OTCScreen
import com.latticeonfhir.android.ui.dispense.prescription.dispenseprescription.DispensePrescriptionScreen
import com.latticeonfhir.android.ui.householdmember.HouseholdMembersScreen
import com.latticeonfhir.android.ui.householdmember.addhouseholdmember.AddHouseholdMember
import com.latticeonfhir.android.ui.householdmember.connectpatient.ConnectPatient
import com.latticeonfhir.android.ui.householdmember.searchresult.SearchResult
import com.latticeonfhir.android.ui.labtestandmedicalrecord.photo.upload.PhotoUploadScreen
import com.latticeonfhir.android.ui.labtestandmedicalrecord.photo.view.PhotoViewScreen
import com.latticeonfhir.android.ui.landingscreen.LandingScreen
import com.latticeonfhir.android.ui.patienteditscreen.address.EditPatientAddress
import com.latticeonfhir.android.ui.patienteditscreen.basicinfo.EditBasicInformation
import com.latticeonfhir.android.ui.patienteditscreen.identification.EditIdentification
import com.latticeonfhir.android.ui.patientlandingscreen.PatientLandingScreen
import com.latticeonfhir.android.ui.patientprofile.PatientProfile
import com.latticeonfhir.android.ui.patientregistration.PatientRegistration
import com.latticeonfhir.android.ui.patientregistration.preview.PatientRegistrationPreview
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.android.ui.prescription.PrescriptionScreen
import com.latticeonfhir.android.ui.prescription.photo.upload.PrescriptionPhotoUploadScreen
import com.latticeonfhir.android.ui.prescription.photo.view.PrescriptionPhotoViewScreen
import com.latticeonfhir.android.ui.searchpatient.SearchPatient
import com.latticeonfhir.android.ui.vitalsscreen.VitalsScreen
import com.latticeonfhir.android.ui.vitalsscreen.addvitals.AddVitalsScreen
import com.latticeonfhir.android.vaccination.navigation.vaccinationNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NavigationAppHost(navController: NavController, startDest: String) {
    NavHost(navController = navController as NavHostController, startDestination = startDest) {
        authNavGraph(
            navController = navController,
            onAuthSuccess = {
                CoroutineScope(Dispatchers.Main).launch {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "loggedIn",
                        true
                    )
                    navController.navigate(Screen.LandingScreen.route) {
                        popUpTo(authRoute)
                    }
                }
            },
        )
        composable(Screen.LandingScreen.route) { LandingScreen(navController = navController) }
        composable(Screen.SearchPatientScreen.route) { SearchPatient(navController = navController) }
        composable(Screen.PatientRegistrationScreen.route) { PatientRegistration(navController = navController) }
        composable(Screen.PatientRegistrationPreviewScreen.route) {
            PatientRegistrationPreview(
                navController = navController
            )
        }
        composable(Screen.PatientLandingScreen.route) { PatientLandingScreen(navController = navController) }
        composable(Screen.HouseholdMembersScreen.route) { HouseholdMembersScreen(navController = navController) }
        composable(Screen.AddHouseholdMember.route) { AddHouseholdMember(navController = navController) }
        composable(Screen.ConfirmRelationship.route) { ConfirmRelationship(navController = navController) }
        composable(Screen.SearchResult.route) { SearchResult(navController = navController) }
        composable(Screen.ConnectPatient.route) { ConnectPatient(navController = navController) }
        composable(Screen.Prescription.route) { PrescriptionScreen(navController = navController) }
        composable(Screen.PatientProfile.route) { PatientProfile(navController = navController) }
        composable(Screen.EditBasicInfo.route) { EditBasicInformation(navController = navController) }
        composable(Screen.EditIdentification.route) { EditIdentification(navController = navController) }
        composable(Screen.EditAddress.route) { EditPatientAddress(navController = navController) }
        composable(Screen.Appointments.route) { AppointmentsScreen(navController = navController) }
        composable(Screen.ScheduleAppointments.route) { ScheduleAppointments(navController = navController) }
        composable(Screen.PrescriptionPhotoUploadScreen.route) {
            PrescriptionPhotoUploadScreen(
                navController = navController
            )
        }
        composable(Screen.PrescriptionPhotoViewScreen.route) {
            PrescriptionPhotoViewScreen(
                navController = navController
            )
        }
        composable(Screen.CVDRiskAssessmentScreen.route) {
            com.latticeonfhir.android.cvd.ui.CVDRiskAssessmentScreen(
                navController
            )
        }
        composable(Screen.VitalsScreen.route) { VitalsScreen(navController = navController) }
        composable(Screen.AddVitalsScreen.route) { AddVitalsScreen(navController = navController) }

        symptomsAndDiagnosisNavGraph(navController)

        composable(Screen.LabAndMedPhotoUploadScreen.route) { PhotoUploadScreen(navController = navController) }
        composable(Screen.LabAndMedRecordPhotoViewScreen.route) { PhotoViewScreen(navController = navController) }

        composable(Screen.DrugDispenseScreen.route) { DrugDispenseScreen(navController = navController) }
        composable(Screen.DispensePrescriptionScreen.route) { DispensePrescriptionScreen(navController = navController) }
        composable(Screen.OTCScreen.route) { OTCScreen(navController = navController) }

        vaccinationNavGraph(navController)
    }
}