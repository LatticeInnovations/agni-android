package com.latticeonfhir.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.ui.vitalsscreen.VitalsScreen
import com.latticeonfhir.core.ui.vitalsscreen.addvitals.AddVitalsScreen
import com.latticeonfhir.features.appointment.navigation.appointmentNavGraph
import com.latticeonfhir.features.auth.navigation.authNavGraph
import com.latticeonfhir.features.cvd.navigation.cvdNavGraph
import com.latticeonfhir.features.dispense.navigation.dispenseNavGraph
import com.latticeonfhir.features.household.navigation.householdNavGraph
import com.latticeonfhir.features.labtestandmedicalrecord.navigation.labTestAndMedRecordNavGraph
import com.latticeonfhir.features.patient.navigation.patientNavGraph
import com.latticeonfhir.features.prescription.navigation.prescriptionNavGraph
import com.latticeonfhir.features.symptomsanddiagnosis.navigation.symptomsAndDiagnosisNavGraph
import com.latticeonfhir.features.vaccination.navigation.vaccinationNavGraph

@Composable
fun NavigationAppHost(navController: NavController, startDest: String) {
    NavHost(navController = navController as NavHostController, startDestination = startDest) {
        authNavGraph(navController)
        patientNavGraph(navController)
        householdNavGraph(navController)
        prescriptionNavGraph(navController)
        symptomsAndDiagnosisNavGraph(navController)
        labTestAndMedRecordNavGraph(navController)
        appointmentNavGraph(navController)
        dispenseNavGraph(navController)
        vaccinationNavGraph(navController)
        cvdNavGraph(navController)

        composable(Screen.VitalsScreen.route) { VitalsScreen(navController = navController) }
        composable(Screen.AddVitalsScreen.route) { AddVitalsScreen(navController = navController) }
    }
}