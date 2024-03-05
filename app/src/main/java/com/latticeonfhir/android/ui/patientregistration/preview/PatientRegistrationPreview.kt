package com.latticeonfhir.android.ui.patientregistration.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.Detail
import com.latticeonfhir.android.ui.common.Heading
import com.latticeonfhir.android.ui.common.Label
import com.latticeonfhir.android.ui.common.ScreenLoader
import com.latticeonfhir.android.utils.constants.NavControllerConstants.CURRENT_STEP
import com.latticeonfhir.android.utils.constants.NavControllerConstants.FROM_HOUSEHOLD_MEMBER
import com.latticeonfhir.android.utils.constants.NavControllerConstants.IS_EDITING
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_FROM
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_ID
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_REGISTER_DETAILS
import com.latticeonfhir.android.utils.constants.NavControllerConstants.RELATION
import com.latticeonfhir.android.utils.constants.NavControllerConstants.RELATIVE_ID
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.VOTER_ID_TYPE
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationEnumFromString
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationPreview(
    navController: NavController,
    viewModel: PatientRegistrationPreviewViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<Patient>(
                    key = PATIENT_REGISTER_DETAILS
                )!!
            if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    key = FROM_HOUSEHOLD_MEMBER
                ) == true
            ) {
                viewModel.fromHouseholdMember = true
                viewModel.relation = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                    key = RELATION
                )!!
                viewModel.patientFrom =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        key = PATIENT_FROM
                    )!!
                viewModel.patientFromId = viewModel.patientFrom!!.id
            }
            viewModel.isLaunched = true
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
                            IS_EDITING,
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            CURRENT_STEP,
                            3
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT_REGISTER_DETAILS,
                            viewModel.patient
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
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
                viewModel.patient?.let { patient ->
                    PreviewScreenRegister(patient) { index ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            IS_EDITING,
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            CURRENT_STEP,
                            index
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT_REGISTER_DETAILS,
                            viewModel.patient
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
                    }
                }
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    viewModel.showLoader = true
                    viewModel.addPatient {
                        viewModel.showLoader = false
                        if (viewModel.fromHouseholdMember) {
                            // adding relation
                            viewModel.addRelation(
                                Relation(
                                    patientId = viewModel.patientFromId,
                                    relativeId = viewModel.relativeId,
                                    relation = getRelationEnumFromString(viewModel.relation)
                                )
                            ) {
                                coroutineScope.launch {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        PATIENT_ID,
                                        viewModel.patientFromId
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        RELATIVE_ID,
                                        viewModel.relativeId
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        RELATION,
                                        viewModel.relation
                                    )
                                    navController.navigate(Screen.ConfirmRelationship.route)
                                }

                            }
                        } else {
                            coroutineScope.launch {
                                navController.popBackStack(Screen.LandingScreen.route, false)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    viewModel.patient
                                )
                                navController.navigate(Screen.PatientLandingScreen.route)
                            }
                        }
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
    if (viewModel.showLoader) ScreenLoader()
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

// TODO: to be removed after binding patient profile
@Composable
private fun PreviewScreenRegister(
    patient: Patient,
    navigate: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
            .testTag("columnLayout")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Heading("Basic Information", 1) { step ->
                    navigate(step)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${patient.nameFirstRep.nameAsSingleString}, ${patient.gender.display}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("NAME_TAG")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Label("Date of birth")
                Detail(patient.birthDate.toPatientPreviewDate(), "DOB_TAG")
                Spacer(modifier = Modifier.height(10.dp))
                Label("Phone No.")
                Detail("+91 ${patient.telecom[0].value}", "PHONE_NO_TAG")
                if (patient.telecom.size > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Email")
                    Detail(patient.telecom[1].value, "EMAIL_TAG")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                Heading("Identification", 2) { step ->
                    navigate(step)
                }
                patient.identifier.forEach { identifier ->
                    when(identifier.system) {
                        PASSPORT_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Passport ID")
                            Detail(identifier.value, "PASSPORT_ID_TAG")
                        }
                        VOTER_ID_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Voter ID")
                            Detail(identifier.value, "VOTER_ID_TAG")
                        }
                        PATIENT_ID_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Patient ID")
                            Detail(identifier.value, "PATIENT_ID_TAG")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            val homeAddressLine1 = patient.addressFirstRep.line[0].value +
                    if (patient.addressFirstRep.line.size > 1) "" else {
                        ", " + patient.addressFirstRep.line[1].value
                    }
            val homeAddressLine2 = patient.addressFirstRep.city +
                    if (patient.addressFirstRep.district.isNullOrBlank()) "" else {
                        ", " + patient.addressFirstRep.district
                    }
            val homeAddressLine3 =
                "${patient.addressFirstRep.state}, ${patient.addressFirstRep.postalCode}"
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Heading("Addresses", 3) { step ->
                    navigate(step)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Label("Home Address")
                Detail(homeAddressLine1, "ADDRESS_LINE1_TAG")
                Detail(homeAddressLine2, "ADDRESS_LINE2_TAG")
                Detail(homeAddressLine3, "ADDRESS_LINE3_TAG")
            }
        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .testTag("end of page")
        )
    }

}