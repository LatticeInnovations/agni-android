package com.latticeonfhir.features.patient.ui.patientregistration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.ui.RelationDialogContent
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.features.patient.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.features.patient.ui.patientregistration.preview.DiscardDialog
import com.latticeonfhir.core.ui.patientregistration.step1.PatientRegistrationStepOne
import com.latticeonfhir.core.ui.patientregistration.step2.PatientRegistrationStepTwo
import com.latticeonfhir.core.ui.patientregistration.step3.PatientRegistrationStepThree
import com.latticeonfhir.core.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.features.patient.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistration(
    navController: NavController,
    viewModel: PatientRegistrationViewModel = viewModel()
) {
    var patientRegister = PatientRegister()
    if (navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        ) == true
    ) {
        viewModel.currentStep = navController.currentBackStackEntry?.savedStateHandle?.get<Int>(
            "currentStep"
        )!!
        viewModel.isEditing = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        )!!
        patientRegister =
            navController.currentBackStackEntry?.savedStateHandle?.get<PatientRegister>(
                "patient_register_details"
            )!!
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
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
    }
    BackHandler {
        if (viewModel.currentStep == 1) navController.navigateUp()
        else viewModel.openDialog = true
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
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
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        if (viewModel.currentStep == 1) navController.navigateUp()
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
                    DiscardDialog(
                        closeDialog = {
                            viewModel.openDialog = false
                        },
                        navigateBack = {
                            viewModel.openDialog = false
                            navController.navigateUp()
                        }
                    )
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
                                navController.navigateUp()
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
