package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.common.IdLength
import com.latticeonfhir.android.ui.common.IdSelectionChip
import com.latticeonfhir.android.utils.constants.IdentificationConstants
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIdentification(
    navController: NavController,
    viewModel: EditIdentificationViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>("patient_details")
            viewModel.isEditing = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                "isEditing"
            ) == true
            viewModel.patient?.run {
                identifier.forEach { identity ->

                    when (identity.identifierType) {
                        IdentificationConstants.PASSPORT_TYPE -> {
                            viewModel.passportId = identity.identifierNumber
                            viewModel.isPassportSelected = viewModel.passportId.isNotBlank()
                        }

                        IdentificationConstants.VOTER_ID_TYPE -> {
                            viewModel.voterId = identity.identifierNumber
                            viewModel.isVoterSelected = viewModel.voterId.isNotBlank()
                        }

                        IdentificationConstants.PATIENT_ID_TYPE -> {
                            viewModel.patientId = identity.identifierNumber
                            viewModel.isPatientSelected = viewModel.patientId.isNotBlank()
                        }

                        else -> {}

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

        }
    }

    BackHandler(enabled = true) {
        navController.previousBackStackEntry?.savedStateHandle?.set("isProfileUpdated", false)
        navController.popBackStack()
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
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            false
                        )
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
                            KeyboardType.Text,
                            KeyboardCapitalization.Characters
                        ) {
                            viewModel.passportId = it
                            if (viewModel.passportId.isNotEmpty())
                                viewModel.isPassportValid =
                                    !viewModel.passportPattern.matches(viewModel.passportId)
                            else
                                viewModel.isPassportValid = false
                        }
                        IdLength(
                            viewModel.passportId,
                            viewModel.maxPassportIdLength,
                            "PASSPORT_ID_LENGTH"
                        )
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
                            KeyboardType.Text,
                            KeyboardCapitalization.Characters
                        ) {
                            viewModel.voterId = it
                            if (viewModel.voterId.isNotEmpty())
                                viewModel.isVoterValid =
                                    !viewModel.voterPattern.matches(viewModel.voterId)
                            else {
                                viewModel.isVoterValid = false
                            }
                        }
                        IdLength(viewModel.voterId, viewModel.maxVoterIdLength, "VOTER_ID_LENGTH")
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
                            stringResource(id = R.string.patient_id_error_msg),
                            KeyboardType.Text,
                            KeyboardCapitalization.Characters
                        ) {
                            viewModel.patientId = it
                            if (viewModel.patientId.isNotEmpty())
                                viewModel.isPatientValid =
                                    viewModel.patientId.length < viewModel.minPatientIdLength
                            else
                                viewModel.isPatientValid = false
                        }
                        IdLength(
                            viewModel.patientId,
                            viewModel.maxPatientIdLength,
                            "PATIENT_ID_LENGTH"
                        )
                    } else {
                        viewModel.patientId = ""
                    }
                }
                viewModel.isEditing = viewModel.checkIsEdit()
            }

        }, floatingActionButton = {
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


                    viewModel.updateBasicInfo(viewModel.patient!!.copy(identifier = viewModel.identifierList))
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "isProfileUpdated",
                        true
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 30.dp),
                enabled = viewModel.identityInfoValidation() && viewModel.isEditing
            ) {
                Text(text = "Save")
            }

        }
    )
}