package com.latticeonfhir.android.navigation

// defining routes
sealed class Screen(val route: String){
    object LandingScreen: Screen("landing_screen")
    object SearchPatientScreen: Screen("search_patient")
    object PatientRegistrationScreen: Screen("patient_registration")
    object PatientRegistrationPreviewScreen: Screen("patient_registration_preview")
}