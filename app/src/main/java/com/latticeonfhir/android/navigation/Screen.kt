package com.latticeonfhir.android.navigation

// defining routes
sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object PhoneEmailScreen : Screen("phone_email_screen")
    object OtpScreen : Screen("otp_screen")
    object LandingScreen : Screen("landing_screen")
    object SearchPatientScreen : Screen("search_patient")
    object PatientRegistrationScreen : Screen("patient_registration")
    object PatientRegistrationPreviewScreen : Screen("patient_registration_preview")
    object PatientLandingScreen : Screen("patient_landing")
    object HouseholdMembersScreen : Screen("household_members")
    object AddHouseholdMember : Screen("add_household_members")
    object ConfirmRelationship : Screen("confirm_relationship")
    object SearchResult : Screen("search_result")
    object ConnectPatient : Screen("connect_patient")
    object Prescription : Screen("prescription")

    object EditPatient : Screen("edit_patient")
    object EditBasicInfo : Screen("edit_basic_info")
    object EditIdentification : Screen("edit_identification")
    object EditAddress : Screen("edit_address")

    object Appointments : Screen("appointments")
    object ScheduleAppointments : Screen("schedule_appointments")
    object RescheduleAppointments : Screen("reschedule_appointments")
}