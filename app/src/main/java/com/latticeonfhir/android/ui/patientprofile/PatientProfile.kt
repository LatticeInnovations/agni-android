package com.latticeonfhir.android.ui.patientprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.latticeonfhir.android.utils.constants.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.IdentificationConstants.VOTER_ID_TYPE
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfile(
    navController: NavController,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    viewModel: PatientProfileViewModel = hiltViewModel()
) {
    viewModel.id =
        navController.previousBackStackEntry?.savedStateHandle?.get<String>(
            key = "patient_detailsID"
        ).toString()
    viewModel.isProfileUpdated =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("isProfileUpdated") == true

    LaunchedEffect(viewModel.isProfileUpdated) {
        withContext(ioDispatcher) {
            if (viewModel.id == "null") viewModel.id = viewModel.patientResponse!!.id
            viewModel.patientResponse = viewModel.getPatientData(viewModel.id)
            viewModel.patientResponse?.run {
                viewModel.firstName = firstName
                viewModel.middleName = middleName ?: ""
                viewModel.lastName = lastName ?: ""
                viewModel.email = email ?: ""
                viewModel.phoneNumber = mobileNumber.toString()
                viewModel.dob = birthDate.toPatientPreviewDate()
                viewModel.gender = gender
                viewModel.identifier = identifier.toMutableList()
                viewModel.passportId = ""
                viewModel.patientId = ""
                viewModel.voterId = ""
                viewModel.identifier.forEach { identity ->
                    when (identity.identifierType) {
                        PASSPORT_TYPE -> {
                            viewModel.passportId = identity.identifierNumber
                        }

                        VOTER_ID_TYPE -> {
                            viewModel.voterId = identity.identifierNumber

                        }

                        PATIENT_ID_TYPE -> {
                            viewModel.patientId = identity.identifierNumber
                        }
                    }
                }

                viewModel.homeAddress.pincode = permanentAddress.postalCode
                viewModel.homeAddress.state = permanentAddress.state
                viewModel.homeAddress.addressLine1 = permanentAddress.addressLine1
                viewModel.homeAddress.addressLine2 = permanentAddress.addressLine2 ?: ""
                viewModel.homeAddress.city = permanentAddress.city
                viewModel.homeAddress.district = permanentAddress.district ?: ""

            }
        }

    }

    val snackBarHostState = remember { SnackbarHostState() }

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
                            Icons.Default.ArrowBack,
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

@Composable
fun PatientProfileScreen(
    viewModel: PatientProfileViewModel,
    navController: NavController
) {
    if (viewModel.patientResponse != null) {
        PreviewScreen(viewModel.patientResponse!!) { step ->
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "isEditing",
                true
            )

            navController.currentBackStackEntry?.savedStateHandle?.set(
                "patient_details",
                viewModel.patientResponse
            )
            when (step) {
                1 -> navController.navigate(Screen.EditBasicInfo.route)
                2 -> navController.navigate(Screen.EditIdentification.route)
                3 -> navController.navigate(Screen.EditAddress.route)
            }
        }
    }
}
