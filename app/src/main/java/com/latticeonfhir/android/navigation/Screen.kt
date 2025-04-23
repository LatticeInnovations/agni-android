package com.latticeonfhir.core.navigation

import com.latticeonfhir.android.auth.navigation.authRoute

sealed class Screen(val route: String) {
    data object PhoneEmailScreen : Screen("$authRoute/phone_email_screen")
    data object LandingScreen : Screen("landing_screen")
    data object SearchPatientScreen : Screen("search_patient")
    data object PatientRegistrationScreen : Screen("patient_registration")
    data object PatientRegistrationPreviewScreen : Screen("patient_registration_preview")
    data object PatientLandingScreen : Screen("patient_landing")
    data object HouseholdMembersScreen : Screen("household_members")
    data object AddHouseholdMember : Screen("add_household_members")
    data object ConfirmRelationship : Screen("confirm_relationship")
    data object SearchResult : Screen("search_result")
    data object ConnectPatient : Screen("connect_patient")
    data object Prescription : Screen("prescription")
    data object PatientProfile : Screen("patient_profile")
    data object EditBasicInfo : Screen("edit_basic_info")
    data object EditIdentification : Screen("edit_identification")
    data object EditAddress : Screen("edit_address")
    data object Appointments : Screen("appointments")
    data object ScheduleAppointments : Screen("schedule_appointments")
    data object PrescriptionPhotoUploadScreen : Screen("prescription_photo")
    data object PrescriptionPhotoViewScreen : Screen("prescription_photo_view")
    data object CVDRiskAssessmentScreen : Screen("cvd_risk_assessment")
    data object VitalsScreen : Screen("vitals_screen")
    data object AddVitalsScreen : Screen("add_vitals_screen")

    data object LabAndMedPhotoUploadScreen : Screen("lab_med_photo")
    data object LabAndMedRecordPhotoViewScreen : Screen("lab_med_photo_view")
}