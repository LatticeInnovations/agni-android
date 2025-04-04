package com.latticeonfhir.android.symptomsanddiagnosis.navigation

const val symptomsAndDiagnosisRoute = "symptomsAndDiagnosis"

sealed class Screen(val route: String) {
    data object SymptomsAndDiagnosisScreen : Screen("$symptomsAndDiagnosisRoute/symptoms_and_diagnosis")
    data object AddSymptomsScreen : Screen("$symptomsAndDiagnosisRoute/add_symptoms")
}