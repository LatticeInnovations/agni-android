package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.ui.main.patientregistration.model.PatientRegister
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistration(
    navController: NavController,
    viewModel: PatientRegistrationViewModel = viewModel()
) {
    var patientRegister = PatientRegister()
    if(navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
        "isEditing"
    ) == true
    ){
        viewModel.step = navController.previousBackStackEntry?.savedStateHandle?.get<Int>(
            "step"
        )!!
        viewModel.isEditing = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        )!!
        patientRegister = navController.previousBackStackEntry?.savedStateHandle?.get<PatientRegister>(
            "patient_register_details"
        )!!
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Patient Registration",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (viewModel.step != 1) {
                        IconButton(onClick = {
                            viewModel.step -= 1
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.LandingScreen.route)
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "clear icon")
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                when (viewModel.step) {
                    1 -> PatientRegistrationStepOne(patientRegister)
                    2 -> PatientRegistrationStepTwo(patientRegister)
                    3 -> PatientRegistrationStepThree(navController, patientRegister)
                }
            }
        }
    )
}
