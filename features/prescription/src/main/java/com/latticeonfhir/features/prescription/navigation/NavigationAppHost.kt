package com.latticeonfhir.features.prescription.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.prescription.ui.PrescriptionScreen
import com.latticeonfhir.features.prescription.ui.photo.upload.PrescriptionPhotoUploadScreen
import com.latticeonfhir.features.prescription.ui.photo.view.PrescriptionPhotoViewScreen

fun NavGraphBuilder.prescriptionNavGraph(
    navController: NavController
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