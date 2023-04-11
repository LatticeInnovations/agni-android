package com.latticeonfhir.android.ui.patientregistration.step4

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import androidx.paging.LoadState
import androidx.paging.compose.items
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.common.PatientItemCard
import com.latticeonfhir.android.utils.relation.Relation
import com.latticeonfhir.android.utils.relation.Relation.getStringFromRelationEnum
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRelationship(
    navController: NavController,
    viewModel: ConfirmRelationshipViewModel = hiltViewModel()
) {
    viewModel.relation = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "relation"
    )!!
    viewModel.patientId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "patientId"
    )!!
    viewModel.relativeId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "relativeId"
    )!!
    viewModel.getPatientData(viewModel.patientId) {
        viewModel.patient = it
    }
    viewModel.getPatientData(viewModel.relativeId) {
        viewModel.relative = it
    }
    viewModel.getRelationBetween(viewModel.patientId, viewModel.relativeId) {
        viewModel.relationBetween = it
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Confirm Relationship",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
//                navigationIcon = {
//                    IconButton(onClick = {
////                                navController.currentBackStackEntry?.savedStateHandle?.set(
////                                    key = "patient_register_details",
////                                    value = patientRegister
////                                )
////                                navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
//
//                        navController.popBackStack()
//                    }) {
//                        Icon(
//                            Icons.Default.ArrowBack,
//                            contentDescription = "Back button"
//                        )
//                    }
//                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.discardAllRelationDialog = true
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
                ConfirmRelationshipScreen(navController, viewModel)
                if (viewModel.discardAllRelationDialog){
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.discardAllRelationDialog = false
                        },
                        title = {
                            Text(
                                text = "Discard relation?",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.testTag("delete dialog title")
                            )
                        },
                        text = {
                                Text(
                                    "Are you sure you want to discard this patient record?",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    //viewModel.deleteRelation(fromPatient!!.id, toPatient!!.id)
                                    viewModel.discardAllRelationDialog = false
                                    viewModel.deleteAllRelation(viewModel.patientId)
                                    Timber.tag("manseeyy").d("deleted all relations")
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "patientId",
                                        viewModel.patientId
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "relativeId",
                                        viewModel.relativeId
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "relation",
                                        viewModel.relation
                                    )
                                    navController.navigate(Screen.LandingScreen.route)
                                }) {
                                Text(
                                    "Yes, discard"
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    viewModel.discardAllRelationDialog = false
                                }) {
                                Text(
                                    "No, go back"
                                )
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun ConfirmRelationshipScreen(
    navController: NavController,
    viewModel: ConfirmRelationshipViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add member",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 4/4",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            if (viewModel.showRelationCard) {
                MemberCard(
                    viewModel.patient, getStringFromRelationEnum(
                        viewModel.relationBetween?.patientIs?.value
                    ),
                    viewModel.relative,
                    viewModel
                ){
                    viewModel.showRelationCard = false
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            if (viewModel.showInverseRelationCard) {
                MemberCard(
                    viewModel.relative,
                    getStringFromRelationEnum(viewModel.relationBetween?.relativeIs?.value),
                    viewModel.patient,
                    viewModel
                ){
                    viewModel.showInverseRelationCard = false
                }
            }
        }
        Button(
            onClick = {
                // add to relation table here
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patientId",
                    viewModel.patientId
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "relativeId",
                    viewModel.relativeId
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "relation",
                    viewModel.relation
                )
                navController.navigate(Screen.LandingScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "Connect Patient")
        }

    }
}

@Composable
fun MemberCard(
    fromPatient: PatientResponse?,
    relation: String?,
    toPatient: PatientResponse?,
    viewModel: ConfirmRelationshipViewModel,
    updateVisibility: () -> (Unit)
) {
    var openDeleteDialog by remember {
        mutableStateOf(false)
    }
    var openEditDialog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.padding(14.dp)
    ) {
        Text(
            text = "${fromPatient?.firstName} is the $relation of ${toPatient?.firstName}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(20.dp))
        FilledTonalIconButton(onClick = { openDeleteDialog = true }) {
            Icon(
                painterResource(id = R.drawable.delete_icon),
                contentDescription = "delete member",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        FilledTonalIconButton(onClick = { openEditDialog = true }) {
            Icon(
                painterResource(id = R.drawable.edit_icon),
                contentDescription = "edit member",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (openDeleteDialog) {
        DeleteDialog(fromPatient, relation, toPatient, viewModel, openDeleteDialog, updateVisibility){
            openDeleteDialog = false
        }
    }

    if (openEditDialog) {
        EditDialog(fromPatient, relation, toPatient, viewModel, openEditDialog){
            openEditDialog = false
        }
    }
}

@Composable
fun DeleteDialog(
    fromPatient: PatientResponse?,
    relation: String?,
    toPatient: PatientResponse?,
    viewModel: ConfirmRelationshipViewModel,
    openDialog: Boolean,
    updateVisibility: () -> Unit,
    closeDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            //viewModel.openDeleteDialog = false
                           closeDialog()
        },
        title = {
            Text(
                text = "Remove relationship?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("delete dialog title")
            )
        },
        text = {
            Column() {
                Text(
                    "Are you sure you want to remove this relationship? Patient records will not be affected.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("delete dialog description 1")
                )
                Text(
                    "${fromPatient?.firstName} is the $relation of ${toPatient?.firstName}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .testTag("delete dialog description 2")
                        .padding(start = 10.dp, top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteRelation(fromPatient!!.id, toPatient!!.id)
                    closeDialog()
                    updateVisibility()
                }) {
                Text(
                    "Confirm",
                    modifier = Modifier.testTag("delete dialog confirm btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    //viewModel.openDeleteDialog = false
                    closeDialog()
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("delete dialog cancel btn")
                )
            }
        }
    )
}

@Composable
fun EditDialog(
    fromPatient: PatientResponse?,
    relation: String?,
    toPatient: PatientResponse?,
    viewModel: ConfirmRelationshipViewModel,
    openDialog: Boolean,
    closeDialog: () -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = {
            //viewModel.openEditDialog = false
            closeDialog()
        },
        title = {
            Text(
                text = "Edit relationship",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("edit dialog title")
            )
        },
        text = {
            Column {
                Text(
                    "${fromPatient?.firstName}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("edit dialog description 1")
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
                            if (fromPatient?.gender == "male") listOf(
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
                            else if (fromPatient?.gender == "female") listOf(
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
                            value = "$relation",
                            onValueChange = {
                                viewModel.relation = it
                            },
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "")
                                }
                            },
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
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
                        text = "of ${toPatient?.firstName}.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    //viewModel.openEditDialog = false
                    closeDialog()
                }) {
                Text(
                    "Save",
                    modifier = Modifier.testTag("edit dialog save btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    //viewModel.openEditDialog = false
                    closeDialog()
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("edit dialog cancel btn")
                )
            }
        }
    )
}