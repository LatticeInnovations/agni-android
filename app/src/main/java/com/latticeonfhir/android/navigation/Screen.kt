package com.latticeonfhir.android.navigation

// defining routes
sealed class Screen(val route: String){
    object LandingScreen: Screen("landing_screen")
    object SearchPatientScreen: Screen("search_patient")
    object PatientRegistrationScreen: Screen("patient_registration")
    object PatientRegistrationPreviewScreen: Screen("patient_registration_preview")
    object PatientLandingScreen: Screen("patient_landing")
    object HouseholdMembersScreen: Screen("household_members")
    object AddHouseholdMember: Screen("add_household_members")
    object ConfirmRelationship: Screen("confirm_relationship")
    object SearchResult: Screen("search_result")
    object ConnectPatient: Screen("connect_patient")
}