package com.latticeonfhir.android.ui.patientregistration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.RelationDialogContent
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.ui.patientregistration.preview.DiscardDialog
import com.latticeonfhir.android.ui.patientregistration.step1.PatientRegistrationStepOne
import com.latticeonfhir.android.ui.patientregistration.step2.PatientRegistrationStepTwo
import com.latticeonfhir.android.ui.patientregistration.step3.PatientRegistrationStepThree
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistration(
    navController: NavController,
    viewModel: PatientRegistrationViewModel = viewModel()
) {
    var patientRegister = PatientRegister()
    if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        ) == true
    ) {
        viewModel.currentStep = navController.previousBackStackEntry?.savedStateHandle?.get<Int>(
            "currentStep"
        )!!
        viewModel.isEditing = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        )!!
        patientRegister =
            navController.previousBackStackEntry?.savedStateHandle?.get<PatientRegister>(
                "patient_register_details"
            )!!
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                "fromHouseholdMember"
            ) == true
        ) {
            viewModel.fromHouseholdMember = true
            viewModel.showRelationDialogue = true
            viewModel.totalSteps = 4
            viewModel.patientFrom =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.patient_registration),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (viewModel.currentStep != 1) {
                        IconButton(onClick = {
                            viewModel.currentStep -= 1
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        if (viewModel.currentStep == 1) navController.popBackStack()
                        else viewModel.openDialog = true
                    }) {
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
                when (viewModel.currentStep) {
                    1 -> PatientRegistrationStepOne(patientRegister)
                    2 -> PatientRegistrationStepTwo(patientRegister)
                    3 -> PatientRegistrationStepThree(navController, patientRegister)
                }
                if (viewModel.openDialog) {
                    DiscardDialog(navController, viewModel.fromHouseholdMember) {
                        viewModel.openDialog = false
                    }
                }
            }
            if (viewModel.showRelationDialogue) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.establish_relation),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.testTag("DIALOG_TITLE")
                            )
                            IconButton(onClick = {
                                viewModel.showRelationDialogue = false
                                if (viewModel.relation.isEmpty()) {
                                    viewModel.fromHouseholdMember = false
                                    viewModel.totalSteps = 3
                                }
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "DIALOG_CLEAR_ICON")
                            }
                        }
                    },
                    text = {
                        RelationDialogContent(
                            NameConverter.getFullName(
                                viewModel.patientFrom?.firstName,
                                viewModel.patientFrom?.middleName,
                                viewModel.patientFrom?.lastName
                            ),
                            "of the patient I am about to create",
                            viewModel.patientFrom?.gender!!,
                            viewModel.relation,
                            expanded
                        ) { update, value ->
                            if (update) viewModel.relation = value
                            expanded = !expanded
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.showRelationDialogue = false
                            },
                            enabled = viewModel.relation.isNotEmpty(),
                            modifier = Modifier.testTag("POSITIVE_BTN")
                        ) {
                            Text(
                                stringResource(id = R.string.create)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.showRelationDialogue = false
                                navController.popBackStack()
                            },
                            modifier = Modifier.testTag("NEGATIVE_BTN")
                        ) {
                            Text(
                                stringResource(id = R.string.go_back)
                            )
                        }
                    }
                )
            }
        }
    )
}
