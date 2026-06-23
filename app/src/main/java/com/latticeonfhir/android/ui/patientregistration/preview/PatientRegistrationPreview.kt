package com.latticeonfhir.android.ui.patientregistration.preview

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.PreviewScreen
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.utils.constants.IdentificationConstants.ABHA_ID_TYPE
import com.latticeonfhir.android.utils.constants.IdentificationConstants.RATION_CARD_TYPE
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationEnumFromString
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.states.getBlockCode
import com.latticeonfhir.android.utils.states.getDistrictCode
import com.latticeonfhir.android.utils.states.getStateCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                viewModel.relation = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
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
                    if (viewModel.fromHouseholdMember) {
                        // adding relation
                        viewModel.addRelation(
                            Relation(
                                patientId = viewModel.patientFromId,
                                relativeId = viewModel.relativeId,
                                relation = getRelationEnumFromString(viewModel.relation)
                            )
                        ) {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "patientId",
                                    viewModel.patientFromId
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "relativeId",
                                    viewModel.relativeId
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "relation",
                                    viewModel.relation
                                )
                                navController.navigate(Screen.ConfirmRelationship.route)
                            }

                        }
                    } else {
                        navController.popBackStack(Screen.LandingScreen.route, false)
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patientResponse!!
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            SELECTED_INDEX,
                            0
                        )
                        navController.navigate(Screen.PatientLandingScreen.route)
                    }
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
    val context = LocalContext.current
    if (patientRegisterDetails != null) {
        viewModel.identifierList.clear()
        if (viewModel.abhaId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = ABHA_ID_TYPE,
                    identifierNumber = viewModel.abhaId,
                    code = null
                )
            )
        }
        if (viewModel.rationCard.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = RATION_CARD_TYPE,
                    identifierNumber = viewModel.rationCard,
                    code = null
                )
            )
        }
        viewModel.patientResponse = PatientResponse(
            id = viewModel.relativeId,
            firstName = viewModel.firstName,
            middleName = viewModel.middleName.ifBlank { null },
            lastName = viewModel.lastName.ifBlank { null },
            birthDate = viewModel.dob.toPatientDate(),
            email = viewModel.email.ifBlank { null },
            active = true,
            gender = viewModel.gender,
            mobileNumber = viewModel.phoneNumber.ifBlank { null }?.toLong(),
            fhirId = null,
            permanentAddress = PatientAddressResponse(
                state = listOf(
                    getStateCode(context, viewModel.homeAddress.state),
                    viewModel.homeAddress.state
                ).joinToString("|"),

                district = listOf(
                    getDistrictCode(
                        context = context,
                        stateName = viewModel.homeAddress.state,
                        districtName = viewModel.homeAddress.district
                    ),
                    viewModel.homeAddress.district
                ).joinToString("|"),

                block = if (viewModel.homeAddress.block.isBlank()) null
                else {
                    listOf(
                        getBlockCode(
                            context = context,
                            stateName = viewModel.homeAddress.state,
                            districtName = viewModel.homeAddress.district,
                            blockName = viewModel.homeAddress.block
                        ),
                        viewModel.homeAddress.block
                    ).joinToString("|")
                },

                city = viewModel.homeAddress.city.ifBlank { null },
                addressLine1 = viewModel.homeAddress.addressLine1.ifBlank { null },
                addressLine2 = viewModel.homeAddress.addressLine2.ifBlank { null },
                country = "India",
                postalCode = viewModel.homeAddress.pincode.ifBlank { null }
            ),
            identifier = viewModel.identifierList
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

private fun setData(patientRegisterDetails: PatientRegister?, viewModel: PatientRegistrationPreviewViewModel) {
    patientRegisterDetails
        ?.run {
            viewModel.firstName = firstName.orEmpty()
            viewModel.middleName = middleName.orEmpty()
            viewModel.lastName = lastName.orEmpty()
            viewModel.email = email.orEmpty()
            viewModel.phoneNumber = phoneNumber.orEmpty()
            viewModel.dobDay = dobDay.orEmpty()
            viewModel.dobMonth = dobMonth.orEmpty()
            viewModel.dobYear = dobYear.orEmpty()
            viewModel.years = years.orEmpty()
            viewModel.months = months.orEmpty()
            viewModel.days = days.orEmpty()
            viewModel.gender = gender.orEmpty()
            viewModel.abhaId = abhaId.orEmpty()
            viewModel.rationCard = rationCard.orEmpty()
            viewModel.homeAddress.pincode = homePostalCode.orEmpty()
            viewModel.homeAddress.state = homeState.orEmpty()
            viewModel.homeAddress.addressLine1 = homeAddressLine1.orEmpty()
            viewModel.homeAddress.addressLine2 = homeAddressLine2.orEmpty()
            viewModel.homeAddress.city = homeCity.orEmpty()
            viewModel.homeAddress.district = homeDistrict.orEmpty()
            viewModel.homeAddress.block = homeBlock.orEmpty()

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