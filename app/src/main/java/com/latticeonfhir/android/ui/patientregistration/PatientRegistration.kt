package com.latticeonfhir.android.ui.patientregistration

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.patientregistration.step1.PatientRegistrationStepOne
import com.latticeonfhir.android.ui.patientregistration.step2.PatientRegistrationStepTwo
import com.latticeonfhir.android.ui.patientregistration.step3.PatientRegistrationStepThree
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationship
import com.latticeonfhir.android.utils.relation.Relation.getRelationEnumFromString

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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Patient Registration",
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
                                contentDescription = "Back button"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        //navController.navigate(Screen.LandingScreen.route)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "clear icon")
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
            }
            if (viewModel.showRelationDialogue) {
                var expanded by remember {
                    mutableStateOf(false)
                }
                AlertDialog(
                    onDismissRequest = {
                        viewModel.showRelationDialogue = false
                    },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Establish relation",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            IconButton(onClick = {
                                viewModel.showRelationDialogue = false
                                if (viewModel.relation.isEmpty()) {
                                    viewModel.fromHouseholdMember = false
                                    viewModel.totalSteps = 3
                                }
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    text = {
                        Column {
                            val name = viewModel.patientFrom?.firstName +
                                    if (viewModel.patientFrom?.middleName.isNullOrEmpty()) "" else {
                                        " " + viewModel.patientFrom?.middleName
                                    } +
                                    if (viewModel.patientFrom?.lastName.isNullOrEmpty()) "" else {
                                        " " + viewModel.patientFrom?.lastName
                                    }
                            Text(
                                name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(23.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "is the",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column() {
                                    val relationsList =
                                        if (viewModel.patientFrom?.gender == "male") listOf(
                                            "Son",
                                            "Father",
                                            "Grand Father",
                                            "Brother",
                                            "Grand Son",
                                            "Uncle",
                                            "Brother-in-law",
                                            "Father-in-law",
                                            "Son-in-law",
                                            "Nephew",
                                            "Husband"
                                        )
                                        else if (viewModel.patientFrom?.gender == "female") listOf(
                                            "Daughter",
                                            "Mother",
                                            "Grand Mother",
                                            "Sister",
                                            "Grand Daughter",
                                            "Aunty",
                                            "Sister-in-law",
                                            "Mother-in-law",
                                            "Daughter-in-law",
                                            "Niece",
                                            "Wife"
                                        )
                                        else listOf(
                                            "Child",
                                            "Parent",
                                            "Grand Parent",
                                            "Sibling",
                                            "Grand Child",
                                            "In-Law",
                                            "Spouse"
                                        )

                                    TextField(
                                        value = viewModel.relation,
                                        onValueChange = {},
                                        trailingIcon = {
                                            IconButton(onClick = { expanded = !expanded }) {
                                                Icon(
                                                    Icons.Default.ArrowDropDown,
                                                    contentDescription = ""
                                                )
                                            }
                                        },
                                        readOnly = true,
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        placeholder = {
                                            Text(
                                                text = "e.g. ${relationsList[0]}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        }.also { interactionSource ->
                                            LaunchedEffect(interactionSource) {
                                                interactionSource.interactions.collect {
                                                    if (it is PressInteraction.Release) {
                                                        expanded = !expanded
                                                    }
                                                }
                                            }
                                        },
                                    )
                                    DropdownMenu(
                                        modifier = Modifier.fillMaxHeight(0.4f),
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        relationsList.forEach { label ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    expanded = false
                                                    viewModel.relation = label
                                                },
                                                text = {
                                                    Text(
                                                        text = label,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.height(23.dp))
                            Text(
                                text = "of the patient I am about to create",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.showRelationDialogue = false
                            },
                            enabled = viewModel.relation.isNotEmpty()
                        ) {
                            Text(
                                "Create"
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.showRelationDialogue = false
                                navController.popBackStack()
                            }) {
                            Text(
                                "Go back"
                            )
                        }
                    }
                )
            }
        }
    )
}
