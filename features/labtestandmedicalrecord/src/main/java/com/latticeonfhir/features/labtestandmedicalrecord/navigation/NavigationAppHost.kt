package com.latticeonfhir.features.labtestandmedicalrecord.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.features.labtestandmedicalrecord.photo.upload.PhotoUploadScreen
import com.latticeonfhir.features.labtestandmedicalrecord.photo.view.PhotoViewScreen

fun NavGraphBuilder.labTestAndMedRecordNavGraph(
    navController: NavController
) {
    composable(Screen.LabAndMedPhotoUploadScreen.route) { PhotoUploadScreen(navController = navController) }
    composable(Screen.LabAndMedRecordPhotoViewScreen.route) { PhotoViewScreen(navController = navController) }
}