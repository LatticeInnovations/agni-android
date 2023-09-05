package com.latticeonfhir.android.ui.patientregistration.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.PreviewScreen
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationEnumFromString
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

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
    val context = LocalContext.current
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
    patientRegisterDetails
        ?.run {
            viewModel.firstName = firstName.toString()
            viewModel.middleName = middleName.toString()
            viewModel.lastName = lastName.toString()
            viewModel.email = email.toString()
            viewModel.phoneNumber = phoneNumber.toString()
            viewModel.dobDay = dobDay.toString()
            viewModel.dobMonth = dobMonth.toString()
            viewModel.dobYear = dobYear.toString()
            viewModel.years = years.toString()
            viewModel.months = months.toString()
            viewModel.days = days.toString()
            viewModel.gender = gender.toString()
            viewModel.passportId = passportId.toString()
            viewModel.voterId = voterId.toString()
            viewModel.patientId = patientId.toString()
            viewModel.homeAddress.pincode = homePostalCode.toString()
            viewModel.homeAddress.state = homeState.toString()
            viewModel.homeAddress.addressLine1 = homeAddressLine1.toString()
            viewModel.homeAddress.addressLine2 = homeAddressLine2.toString()
            viewModel.homeAddress.city = homeCity.toString()
            viewModel.homeAddress.district = homeDistrict.toString()
            viewModel.workAddress.pincode = workPostalCode.toString()
            viewModel.workAddress.state = workState.toString()
            viewModel.workAddress.addressLine1 = workAddressLine1.toString()
            viewModel.workAddress.addressLine2 = workAddressLine2.toString()
            viewModel.workAddress.city = workCity.toString()
            viewModel.workAddress.district = workDistrict.toString()

            if (dobAgeSelector == "dob") {
                viewModel.dob = "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}"
            } else {
                viewModel.dob = ageToPatientDate(
                    viewModel.years.toInt(),
                    viewModel.months.toInt(),
                    viewModel.days.toInt()
                )
                Timber.d("manseeyy ${viewModel.dob.toPatientDate()}")
            }
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
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "isEditing",
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "currentStep",
                            3
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient_register_details",
                            patientRegisterDetails
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
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
                if (patientRegisterDetails != null) {
                    viewModel.identifierList.clear()
                    if (viewModel.passportId.isNotEmpty()) {
                        viewModel.identifierList.add(
                            PatientIdentifier(
                                identifierType = context.getString(R.string.passport_id_web_link),
                                identifierNumber = viewModel.passportId,
                                code = null
                            )
                        )
                    }
                    if (viewModel.voterId.isNotEmpty()) {
                        viewModel.identifierList.add(
                            PatientIdentifier(
                                identifierType = context.getString(R.string.voter_id_web_link),
                                identifierNumber = viewModel.voterId,
                                code = null
                            )
                        )
                    }
                    if (viewModel.patientId.isNotEmpty()) {
                        viewModel.identifierList.add(
                            PatientIdentifier(
                                identifierType = context.getString(R.string.patient_id_web_link),
                                identifierNumber = viewModel.patientId,
                                code = null
                            )
                        )
                    }
                    viewModel.patientResponse = PatientResponse(
                        id = viewModel.relativeId,
                        firstName = viewModel.firstName,
                        middleName = if (viewModel.middleName.isEmpty()) null else viewModel.middleName,
                        lastName = if (viewModel.lastName.isEmpty()) null else viewModel.lastName,
                        birthDate = viewModel.dob.toPatientDate(),
                        email = if (viewModel.email.isEmpty()) null else viewModel.email,
                        active = true,
                        gender = viewModel.gender,
                        mobileNumber = viewModel.phoneNumber.toLong(),
                        fhirId = null,
                        permanentAddress = PatientAddressResponse(
                            postalCode = viewModel.homeAddress.pincode,
                            state = viewModel.homeAddress.state,
                            addressLine1 = viewModel.homeAddress.addressLine1,
                            addressLine2 = if (viewModel.homeAddress.addressLine2.isEmpty()) null else viewModel.homeAddress.addressLine2,
                            city = viewModel.homeAddress.city,
                            country = "India",
                            district = if (viewModel.homeAddress.district.isEmpty()) null else viewModel.homeAddress.district
                        ),
                        identifier = viewModel.identifierList
                    )
                    PreviewScreen(
                        viewModel.patientResponse!!
                    ) { index ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "isEditing",
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "currentStep",
                            index
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient_register_details",
                            patientRegisterDetails
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
                    }
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
                            "patient",
                            viewModel.patientResponse!!
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
fun DiscardDialog(navController: NavController, fromHousehold: Boolean, closeDialog: () -> Unit) {
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
                    closeDialog()
                    if (fromHousehold)
                        navController.popBackStack(Screen.AddHouseholdMember.route, false)
                    else navController.popBackStack(Screen.LandingScreen.route, false)
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