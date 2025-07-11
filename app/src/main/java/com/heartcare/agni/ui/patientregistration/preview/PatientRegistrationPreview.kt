package com.heartcare.agni.ui.patientregistration.preview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.NationalIdUse
import com.heartcare.agni.data.server.model.patient.PatientAddressResponse
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.preview.PreviewScreen
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.utils.constants.IdentificationConstants.HOSPITAL_ID
import com.heartcare.agni.utils.constants.IdentificationConstants.NATIONAL_ID
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPatientDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationPreview(
    navController: NavController,
    viewModel: PatientRegistrationPreviewViewModel = hiltViewModel()
) {
    val patientRegisterDetails =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientRegister>(
            key = "patient_register_details"
        )
    setData(patientRegisterDetails, viewModel)
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    key = "fromHouseholdMember"
                ) == true
            ) {
                viewModel.fromHouseholdMember = true
                viewModel.relation =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                        key = "relation"
                    )!!
                viewModel.patientFrom =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        key = "patientFrom"
                    )!!
                viewModel.patientFromId = viewModel.patientFrom!!.id
            }
            viewModel.isLaunched = true
        }
    }
    BackHandler {
        navController.previousBackStackEntry?.savedStateHandle?.set(
            "isEditing",
            true
        )
        navController.previousBackStackEntry?.savedStateHandle?.set(
            "currentStep",
            3
        )
        navController.previousBackStackEntry?.savedStateHandle?.set(
            "patient_register_details",
            patientRegisterDetails
        )
        navController.navigateUp()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.preview),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isEditing",
                            true
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "currentStep",
                            3
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "patient_register_details",
                            patientRegisterDetails
                        )
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "BACK_ICON"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = { viewModel.openDialog = true }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
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
                PreviewScreenComposable(patientRegisterDetails, viewModel, navController)
                if (viewModel.openDialog) {
                    DiscardDialog(
                        closeDialog = {
                            viewModel.openDialog = false
                        },
                        navigateBack = {
                            viewModel.openDialog = false
                            navController.popBackStack(Screen.PatientRegistrationScreen.route, true)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    viewModel.addPatient(
                        viewModel.patientResponse!!
                    )
                    navController.popBackStack(Screen.LandingScreen.route, false)
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        PATIENT,
                        viewModel.patientResponse!!
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        SELECTED_INDEX,
                        0
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        PATIENT_SAVED,
                        true
                    )
                    navController.navigate(Screen.PatientLandingScreen.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    )
}

@Composable
private fun PreviewScreenComposable(
    patientRegisterDetails: PatientRegister?,
    viewModel: PatientRegistrationPreviewViewModel,
    navController: NavController
) {
    if (patientRegisterDetails != null) {
        viewModel.identifierList.clear()
        if (viewModel.hospitalId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = HOSPITAL_ID,
                    identifierNumber = viewModel.hospitalId,
                    code = null,
                    use = null
                )
            )
        }
        if (viewModel.nationalId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = NATIONAL_ID,
                    identifierNumber = viewModel.nationalId,
                    code = null,
                    use = if (viewModel.isNationalIdVerified) NationalIdUse.OFFICIAL.use
                    else NationalIdUse.TEMP.use
                )
            )
        }
        viewModel.patientResponse = PatientResponse(
            id = viewModel.relativeId,
            firstName = viewModel.firstName,
            lastName = viewModel.lastName,
            birthDate = viewModel.dob.toPatientDate(),
            gender = viewModel.gender,
            mobileNumber = viewModel.phoneNumber.ifBlank { null },
            mothersName = viewModel.motherName,
            fathersName = viewModel.fatherName.ifBlank { null },
            spouseName = viewModel.spouseName.ifBlank { null },
            fhirId = null,
            generalPractitioner = null,
            managingOrganization = null,
            isDeleted = null,
            permanentAddress = PatientAddressResponse(
                postalCode = viewModel.postalCode.ifBlank { null },
                province = viewModel.province!!.fhirId,
                areaCouncil = viewModel.areaCouncil!!.fhirId,
                island = viewModel.island!!.fhirId,
                village = viewModel.village?.fhirId,
                addressLine2 = viewModel.otherVillage.ifBlank { null },
                country = "Vanuatu"
            ),
            identifier = viewModel.identifierList,
            patientDeceasedReasonId = null,
            patientDeceasedReason = viewModel.selectedDeceasedReason.ifBlank { null },
            appUpdatedDate = Date(),
            active = true
        )
        PreviewScreen(
            viewModel.patientResponse!!
        ) { index ->
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "isEditing",
                true
            )
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "currentStep",
                index
            )
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "patient_register_details",
                patientRegisterDetails
            )
            navController.navigateUp()
        }
    }
}

private fun setData(
    patientRegisterDetails: PatientRegister?,
    viewModel: PatientRegistrationPreviewViewModel
) {
    patientRegisterDetails
        ?.run {
            viewModel.firstName = firstName.toString()
            viewModel.lastName = lastName.toString()
            viewModel.phoneNumber = phoneNumber.toString()
            viewModel.dobDay = dobDay.toString()
            viewModel.dobMonth = dobMonth.toString()
            viewModel.dobYear = dobYear.toString()
            viewModel.years = years.toString()
            viewModel.months = months.toString()
            viewModel.days = days.toString()
            viewModel.gender = gender.toString()
            viewModel.isPersonDeceased = isPersonDeceased ?: 0
            viewModel.selectedDeceasedReason = personDeceasedReason.toString()
            viewModel.motherName = motherName.toString()
            viewModel.fatherName = fatherName.toString()
            viewModel.spouseName = spouseName.toString()

            viewModel.hospitalId = hospitalId.toString()
            viewModel.nationalId = nationalId.toString()
            viewModel.isNationalIdVerified = nationalIdUse == NationalIdUse.OFFICIAL.use

            viewModel.province = province
            viewModel.areaCouncil = areaCouncil
            viewModel.island = island
            viewModel.village = village
            viewModel.otherVillage = otherVillage.toString()
            viewModel.postalCode = postalCode.toString()

            if (dobAgeSelector == "dob") {
                viewModel.dob = "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}"
            } else {
                viewModel.dob = ageToPatientDate(
                    viewModel.years.toIntOrNull() ?: 0,
                    viewModel.months.toIntOrNull() ?: 0,
                    viewModel.days.toIntOrNull() ?: 0
                )
            }
        }
}

@Composable
fun DiscardDialog(
    closeDialog: () -> Unit,
    navigateBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = stringResource(id = R.string.discard_changes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("alert dialog title")
            )
        },
        text = {
            Text(
                stringResource(id = R.string.discard_dialog_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("alert dialog description")
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    navigateBack()
                }) {
                Text(
                    stringResource(id = R.string.yes_discard),
                    modifier = Modifier.testTag("alert dialog confirm btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    stringResource(id = R.string.no_go_back),
                    modifier = Modifier.testTag("alert dialog cancel btn")
                )
            }
        }
    )
}