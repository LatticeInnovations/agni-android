package com.latticeonfhir.android.vaccination.navigation

const val vaccinationRoute = "vaccination"

sealed class Screen(val route: String) {
    data object VaccinationScreen : Screen("$vaccinationRoute/vaccination_screen")
    data object AddVaccinationScreen : Screen("$vaccinationRoute/add_vaccination_screen")
    data object ViewVaccinationScreen : Screen("$vaccinationRoute/view_vaccination_screen")
    data object VaccinationErrorScreen : Screen("$vaccinationRoute/vaccination_error_screen")
}