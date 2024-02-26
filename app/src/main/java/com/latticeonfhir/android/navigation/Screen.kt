package com.latticeonfhir.android.navigation

sealed class Screen(val route: String) {
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

    object PatientProfile : Screen("patient_profile")
    object EditBasicInfo : Screen("edit_basic_info")
    object EditIdentification : Screen("edit_identification")
    object EditAddress : Screen("edit_address")

    object Appointments : Screen("appointments")
    object ScheduleAppointments : Screen("schedule_appointments")
}