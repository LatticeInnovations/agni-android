package com.latticeonfhir.android.prescription.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.latticeonfhir.android.prescription.ui.PrescriptionScreen
import com.latticeonfhir.android.prescription.ui.photo.upload.PrescriptionPhotoUploadScreen
import com.latticeonfhir.android.prescription.ui.photo.view.PrescriptionPhotoViewScreen

fun NavGraphBuilder.prescriptionNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.PrescriptionPhotoViewScreen.route,
        route = prescriptionRoute
    ) {
        composable(Screen.Prescription.route) {
            PrescriptionScreen(
                navController = navController
            )
        }
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
    }
}