package com.latticeonfhir.android.prescription.navigation

const val prescriptionRoute = "prescription"

sealed class Screen(val route: String) {
    data object Prescription : Screen("$prescriptionRoute/prescription_screen")
    data object PrescriptionPhotoUploadScreen : Screen("$prescriptionRoute/prescription_photo")
    data object PrescriptionPhotoViewScreen : Screen("$prescriptionRoute/prescription_photo_view")
}