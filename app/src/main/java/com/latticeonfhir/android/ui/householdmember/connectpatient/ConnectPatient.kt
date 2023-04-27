package com.latticeonfhir.android.ui.householdmember.connectpatient

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.data.local.constants.Constants
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.relation.RelationConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectPatient(
    navController: NavController,
    viewModel: ConnectPatientViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patientFrom =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patientFrom"
                )
            viewModel.selectedMembersList =
                navController.previousBackStackEntry?.savedStateHandle?.get<MutableList<PatientResponse>>(
                    "selectedMembersList"
                )!!.toMutableList()
            viewModel.membersList =
                mutableStateListOf(*viewModel.selectedMembersList.toTypedArray())
        }
        viewModel.isLaunched = true
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Connect patient to",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = Constants.GetFullName(
                                viewModel.patientFrom?.firstName,
                                viewModel.patientFrom?.middleName,
                                viewModel.patientFrom?.lastName
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.connectedMembersList.isEmpty()) navController.popBackStack()
                        else viewModel.discardAllRelationDialog = true
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "back")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 55.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AnimatedVisibility(
                        visible = viewModel.connectedMembersList.isNotEmpty(),
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column() {
                            Text(
                                text = "Connected Patients",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            viewModel.connectedMembersList.forEach { relationView ->
                                ConnectedMemberCard(context, relationView, viewModel)
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                    if (viewModel.membersList.isNotEmpty()) {
                        Text(
                            text = "Patients to connect",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        viewModel.membersList.forEach { member ->
                            if (member != null) {
                                PatientRow(member, viewModel)
                            }
                        }
                    }
                }
                if (viewModel.discardAllRelationDialog) {
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
                                    viewModel.discardAllRelationDialog = false
                                    viewModel.discardRelations()
                                    navController.popBackStack()
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

                if (viewModel.showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.showConfirmDialog = false
                        },
                        title = {
                            Text(
                                text = "Connect patient",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.testTag("delete dialog title")
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = "You have also selected",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                                    items(viewModel.selectedMembersList) { member ->
                                        val color = MaterialTheme.colorScheme.onSurface
                                        Row(
                                            Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Canvas(
                                                modifier = Modifier
                                                    .padding(
                                                        start = 8.dp,
                                                        end = 8.dp
                                                    )
                                                    .size(4.dp)
                                            ) {
                                                drawCircle(color = color)
                                            }
                                            Text(
                                                text = Constants.GetFullName(
                                                    member?.firstName,
                                                    member?.middleName,
                                                    member?.lastName
                                                ),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "These persons are not connected to the others.",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.addRelationsToGenericEntity()
                                    navController.popBackStack(
                                        Screen.HouseholdMembersScreen.route,
                                        false
                                    )
                                }) {
                                Text(
                                    "Proceed anyway"
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    viewModel.showConfirmDialog = false
                                }) {
                                Text(
                                    "Go back"
                                )
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            Button(
                onClick = {
                    if (viewModel.selectedMembersList.isEmpty()) navController.popBackStack(
                        Screen.HouseholdMembersScreen.route,
                        false
                    )
                    else {
                        viewModel.showConfirmDialog = true
                    }

                },
                enabled = viewModel.connectedMembersList.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp)
            ) {
                Text(text = "Confirm", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun ConnectedMemberCard(
    context: Context,
    relationView: RelationView,
    viewModel: ConnectPatientViewModel
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
            text = "${
                Constants.GetFullName(
                    relationView.patientFirstName,
                    relationView.patientMiddleName,
                    relationView.patientLastName
                )
            } " +
                    "is the ${
                        RelationConverter.getRelationFromRelationEnum(
                            context,
                            relationView.relation
                        )
                    } of " +
                    "${
                        Constants.GetFullName(
                            relationView.relativeFirstName,
                            relationView.relativeMiddleName,
                            relationView.relativeLastName
                        )
                    }.",
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
        DeleteDialog(
            context,
            relationView,
            viewModel
        ) {
            openDeleteDialog = false
        }
    }

    if (openEditDialog) {
        EditDialog(
            context,
            relationView,
            viewModel
        ) {
            openEditDialog = false
        }
    }
}

@Composable
fun DeleteDialog(
    context: Context,
    relationView: RelationView,
    viewModel: ConnectPatientViewModel,
    closeDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Remove relationship?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column() {
                Text(
                    "Are you sure you want to remove this relationship? Patient records will not be affected.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "${
                        Constants.GetFullName(
                            relationView.patientFirstName,
                            relationView.patientMiddleName,
                            relationView.patientLastName
                        )
                    } " +
                            "is the ${
                                RelationConverter.getRelationFromRelationEnum(
                                    context,
                                    relationView.relation
                                )
                            } of " +
                            "${
                                Constants.GetFullName(
                                    relationView.relativeFirstName,
                                    relationView.relativeMiddleName,
                                    relationView.relativeLastName
                                )
                            }.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.removeRelationBetween(
                        relationView.patientId,
                        relationView.relativeId
                    ) {
                        viewModel.deleteRelation(relationView.patientId, relationView.relativeId) {
                            viewModel.getRelationBetween(
                                relationView.patientId,
                                relationView.relativeId
                            )
                        }
                    }
                    closeDialog()
                }) {
                Text(
                    "Confirm"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    "Cancel"
                )
            }
        }
    )
}

@Composable
fun EditDialog(
    context: Context,
    relationView: RelationView,
    viewModel: ConnectPatientViewModel,
    closeDialog: () -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var relation by remember {
        mutableStateOf(
            RelationConverter.getRelationFromRelationEnum(context, relationView.relation)
                .capitalize()
        )
    }
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Edit relationship",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    Constants.GetFullName(
                        relationView.patientFirstName,
                        relationView.patientMiddleName,
                        relationView.patientLastName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
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
                            Constants.GetRelationshipList(relationView.patientGender)

                        TextField(
                            value = relation,
                            onValueChange = {
                                relation = it
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
                                        relation = label
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
                    text = "of ${
                        Constants.GetFullName(
                            relationView.relativeFirstName,
                            relationView.relativeMiddleName,
                            relationView.relativeLastName
                        )
                    }.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.removeRelationBetween(
                        relationView.patientId,
                        relationView.relativeId
                    ) {
                        viewModel.updateRelation(
                            Relation(
                                patientId = relationView.patientId,
                                relation = RelationConverter.getRelationEnumFromString(relation),
                                relativeId = relationView.relativeId
                            )
                        )
                    }
                    closeDialog()
                }) {
                Text(
                    "Save"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    "Cancel"
                )
            }
        }
    )
}

@Composable
fun PatientRow(member: PatientResponse, viewModel: ConnectPatientViewModel) {
    var showConnectDialog by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable {
                showConnectDialog = true
            },
        shape = RoundedCornerShape(1.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = Constants.GetFullName(
                    member.firstName,
                    member.middleName,
                    member.lastName
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${member.gender[0].uppercase()}/${Constants.GetAge(member.birthDate)} · PID ${member.fhirId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = Constants.GetAddress(member.permanentAddress),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (showConnectDialog) {
        ConnectMemberDialog(member, viewModel) {
            showConnectDialog = false
        }
    }
}

@Composable
fun ConnectMemberDialog(
    member: PatientResponse,
    viewModel: ConnectPatientViewModel,
    closeDialog: () -> (Unit)
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var relation by remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Connect patient",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    Constants.GetFullName(
                        viewModel.patientFrom?.firstName,
                        viewModel.patientFrom?.middleName,
                        viewModel.patientFrom?.lastName
                    ),
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
                    Column {
                        val relationsList =
                            viewModel.patientFrom?.gender?.let { Constants.GetRelationshipList(it) }
                        TextField(
                            value = relation,
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
                                    text = "e.g. ${relationsList?.get(0)}",
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
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(0.4f),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            relationsList?.forEach { label ->
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        relation = label
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
                    text = "of ${
                        Constants.GetFullName(
                            member.firstName,
                            member.middleName,
                            member.lastName
                        )
                    }.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // call add member to household function here
                    viewModel.addRelation(
                        Relation(
                            patientId = viewModel.patientFrom!!.id,
                            relativeId = member.id,
                            relation = RelationConverter.getRelationEnumFromString(relation)
                        )
                    ) {
                        viewModel.getRelationBetween(viewModel.patientFrom?.id!!, member.id)
                        viewModel.selectedMembersList.remove(member)
                        viewModel.membersList =
                            mutableStateListOf(*viewModel.selectedMembersList.toTypedArray())
                    }
                    closeDialog()
                },
                enabled = relation.isNotEmpty()
            ) {
                Text(
                    "Create"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    "Go back"
                )
            }
        }
    )
}