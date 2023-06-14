package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.utils.constants.IdentificationConstants
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIdentification(
    navController: NavController,
    viewModel: EditIdentificationViewModel = hiltViewModel()
) {
    val patientResponse =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>("patient_details")
    viewModel.patient = patientResponse
    viewModel.isEditing = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
        "isEditing"
    ) == true
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientResponse?.run {
                identifier.forEach { identity ->
                    Timber.tag("identifier")
                        .d(
                            "%s%s",
                            identity.identifierType + "  " + identity.identifierNumber + " ",
                            identity.code
                        )
                    when (identity.identifierType) {
                        IdentificationConstants.PASSPORT_TYPE -> {
                            viewModel.passportId = identity.identifierNumber
                            viewModel.isPassportSelected = viewModel.passportId.isNotEmpty()
                        }

                        IdentificationConstants.VOTER_ID_TYPE -> {
                            viewModel.voterId = identity.identifierNumber

                        }

                        IdentificationConstants.PATIENT_ID_TYPE -> {
                            viewModel.patientId = identity.identifierNumber
                        }

                        else -> {}

                    }

                    if (viewModel.isEditing) {
                        viewModel.isPassportSelected = viewModel.passportId.isNotEmpty()
                        viewModel.isVoterSelected = viewModel.voterId.isNotEmpty()
                        viewModel.isPatientSelected = viewModel.patientId.isNotEmpty()
                    }
                }

                viewModel.isPassportSelectedTemp = viewModel.isPassportSelected
                viewModel.isVoterSelectedTemp = viewModel.isVoterSelected
                viewModel.isPatientSelectedTemp = viewModel.isPatientSelected
                viewModel.passportIdTemp = viewModel.passportId
                viewModel.voterIdTemp = viewModel.voterId
                viewModel.patientIdTemp = viewModel.patientId


            }
            viewModel.isLaunched = true
            FhirApp.isProfileUpdated = false

        }
    }


    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Identification",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()

                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "clear icon"
                        )
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    Text(
                        text = "Undo all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.isEditing) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(viewModel.isEditing, onClick = {
                                if (viewModel.revertChanges()) {
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar("Changes undone")

                                    }
                                }
                            })

                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(it)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        IdSelectionChip(
                            idSelected = viewModel.isPassportSelected,
                            label = "Passport Id"
                        ) {
                            viewModel.isPassportSelected = !it
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        IdSelectionChip(
                            idSelected = viewModel.isVoterSelected,
                            label = "Voter Id"
                        ) {
                            viewModel.isVoterSelected = !it
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        IdSelectionChip(
                            idSelected = viewModel.isPatientSelected,
                            label = "Patient Id"
                        ) {
                            viewModel.isPatientSelected = !it
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (viewModel.isPassportSelected) {
                        Spacer(modifier = Modifier.height(5.dp))
                        CustomTextField(
                            value = viewModel.passportId,
                            label = "Passport Id",
                            weight = 1f,
                            viewModel.maxPassportIdLength,
                            viewModel.isPassportValid,
                            "Enter valid Passport ID (eg., A1098765)",
                            KeyboardType.Text
                        ) {
                            viewModel.passportId = it
                            if (viewModel.passportId.isNotEmpty())
                                viewModel.isPassportValid =
                                    !viewModel.passportPattern.matches(viewModel.passportId)
                            else
                                viewModel.isPassportValid = false
                        }
                        IdLength(viewModel.passportId, viewModel.maxPassportIdLength)
                    } else {
                        viewModel.passportId = ""
                    }
                    if (viewModel.isVoterSelected) {
                        Spacer(modifier = Modifier.height(5.dp))
                        CustomTextField(
                            value = viewModel.voterId,
                            label = "Voter Id",
                            weight = 1f,
                            viewModel.maxVoterIdLength,
                            viewModel.isVoterValid,
                            "Enter valid Voter Id (eg., XYZ9876543)",
                            KeyboardType.Text
                        ) {
                            viewModel.voterId = it
                            if (viewModel.voterId.isNotEmpty())
                                viewModel.isVoterValid =
                                    !viewModel.voterPattern.matches(viewModel.voterId)
                            else {
                                viewModel.isVoterValid = false
                            }
                        }
                        IdLength(viewModel.voterId, viewModel.maxVoterIdLength)
                    } else {
                        viewModel.voterId = ""
                    }
                    if (viewModel.isPatientSelected) {
                        Spacer(modifier = Modifier.height(5.dp))
                        CustomTextField(
                            value = viewModel.patientId,
                            label = "Patient Id",
                            weight = 1f,
                            viewModel.maxPatientIdLength,
                            viewModel.isPatientValid,
                            "Patient Id length should be 10.",
                            KeyboardType.Text
                        ) {
                            viewModel.patientId = it
                            if (viewModel.patientId.isNotEmpty())
                                viewModel.isPatientValid = viewModel.patientId.length < 10
                            else
                                viewModel.isPatientValid = false
                        }
                        IdLength(viewModel.patientId, viewModel.maxPatientIdLength)
                    } else {
                        viewModel.patientId = ""
                    }
                }
                viewModel.isEditing = viewModel.checkIsEdit()
                Button(
                    onClick = {
                        if (viewModel.passportId.isNotEmpty() && viewModel.isPassportSelected) {
                            viewModel.identifierList.add(
                                PatientIdentifier(
                                    identifierType = IdentificationConstants.PASSPORT_TYPE,
                                    identifierNumber = viewModel.passportId,
                                    code = null
                                )
                            )
                        }
                        if (viewModel.voterId.isNotEmpty() && viewModel.isVoterSelected) {
                            viewModel.identifierList.add(
                                PatientIdentifier(
                                    identifierType = IdentificationConstants.VOTER_ID_TYPE,
                                    identifierNumber = viewModel.voterId,
                                    code = null
                                )
                            )
                        }
                        if (viewModel.patientId.isNotEmpty() && viewModel.isPatientSelected) {
                            viewModel.identifierList.add(
                                PatientIdentifier(
                                    identifierType = IdentificationConstants.PATIENT_ID_TYPE,
                                    identifierNumber = viewModel.patientId,
                                    code = null
                                )
                            )
                        }


                        viewModel.updateBasicInfo(patientResponse!!.copy(identifier = viewModel.identifierList))
                        navController.popBackStack()
                        FhirApp.isProfileUpdated = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = viewModel.identityInfoValidation() && viewModel.isEditing
                ) {
                    Text(text = "Save")
                }
            }

        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdSelectionChip(idSelected: Boolean, label: String, updateSelection: (Boolean) -> Unit) {
    FilterChip(
        selected = idSelected,
        onClick = {
            updateSelection(idSelected)
        },
        label = { Text(text = label) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            selectedBorderWidth = 1.dp
        ),
        leadingIcon = {
            if (idSelected)
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
        },
        modifier = Modifier.testTag("$label chip")
    )
}

@Composable
fun IdLength(idName: String, requiredLength: Int) {
    Text(
        text = "${idName.length}/$requiredLength",
        textAlign = TextAlign.Right,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}