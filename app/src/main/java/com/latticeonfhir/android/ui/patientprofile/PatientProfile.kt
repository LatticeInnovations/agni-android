package com.latticeonfhir.android.ui.patientprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.PreviewScreen
import com.latticeonfhir.android.utils.constants.NavControllerConstants.IS_EDITING
import com.latticeonfhir.android.utils.constants.NavControllerConstants.IS_PROFILE_UPDATED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import org.hl7.fhir.r4.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfile(
    navController: NavController,
    viewModel: PatientProfileViewModel = hiltViewModel()
) {
    viewModel.isProfileUpdated =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(IS_PROFILE_UPDATED) == true
    LaunchedEffect(viewModel.isLaunched){
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<Patient>(
                    key = PATIENT
                )!!
            viewModel.isLaunched = true
        }
    }
    LaunchedEffect(viewModel.isProfileUpdated) {
        viewModel.getPatientData()
    }

    val snackBarHostState = remember { SnackbarHostState() }
    if (viewModel.isLaunched) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Patient profile",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    )

                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    PatientProfileScreen(viewModel, navController)

                    if (viewModel.isProfileUpdated) {
                        LaunchedEffect(true) {
                            snackBarHostState.showSnackbar("Profile updated successfully")
                            viewModel.isProfileUpdated = false
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun PatientProfileScreen(
    viewModel: PatientProfileViewModel,
    navController: NavController
) {
    PreviewScreen(viewModel.patient) { step ->
        navController.currentBackStackEntry?.savedStateHandle?.set(
            IS_EDITING,
            true
        )

        navController.currentBackStackEntry?.savedStateHandle?.set(
            PATIENT,
            viewModel.patient
        )
        when (step) {
            1 -> navController.navigate(Screen.EditBasicInfo.route)
            2 -> navController.navigate(Screen.EditIdentification.route)
            3 -> navController.navigate(Screen.EditAddress.route)
        }
    }
}
