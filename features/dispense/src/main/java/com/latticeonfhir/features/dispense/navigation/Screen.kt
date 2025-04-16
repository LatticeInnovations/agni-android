package com.latticeonfhir.features.dispense.navigation

const val dispenseRoute = "dispense"

sealed class Screen(val route: String) {
    data object DrugDispenseScreen : Screen("$dispenseRoute/drugs_dispense")
    data object DispensePrescriptionScreen : Screen("$dispenseRoute/dispense_prescription")
    data object OTCScreen : Screen("$dispenseRoute/otc_screen")
}