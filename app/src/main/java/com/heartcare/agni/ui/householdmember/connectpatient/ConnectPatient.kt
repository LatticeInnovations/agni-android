package com.heartcare.agni.ui.householdmember.connectpatient

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.relation.Relation
import com.heartcare.agni.data.local.roomdb.views.RelationView
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.DiscardAllRelationDialog
import com.heartcare.agni.ui.common.RelationDialogContent
import com.heartcare.agni.utils.converters.responseconverter.AddressConverter
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import com.heartcare.agni.utils.converters.responseconverter.RelationConverter
import com.heartcare.agni.utils.converters.responseconverter.RelationshipList
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.util.Locale

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
                            text = stringResource(id = R.string.connect_to),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = NameConverter.getFullName(
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
                        Column {
                            Text(
                                text = stringResource(id = R.string.connected_patients),
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
                            text = stringResource(id = R.string.patients_to_connect),
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
                    DiscardAllRelationDialog { discard ->
                        if (discard) {
                            viewModel.discardRelations()
                            navController.popBackStack()
                        }
                        viewModel.discardAllRelationDialog = false
                    }
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
                                                text = NameConverter.getFullName(
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
                    if (viewModel.selectedMembersList.isEmpty()) {
                        viewModel.addRelationsToGenericEntity()
                        navController.popBackStack(
                            Screen.HouseholdMembersScreen.route,
                            false
                        )
                    } else {
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
                NameConverter.getFullName(
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
                        NameConverter.getFullName(
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
            Column {
                Text(
                    "Are you sure you want to remove this relationship? Patient records will not be affected.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "${
                        NameConverter.getFullName(
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
                                NameConverter.getFullName(
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
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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
            RelationDialogContent(
                NameConverter.getFullName(
                    relationView.patientFirstName,
                    relationView.patientMiddleName,
                    relationView.patientLastName
                ),
                "of ${
                    NameConverter.getFullName(
                        relationView.relativeFirstName,
                        relationView.relativeMiddleName,
                        relationView.relativeLastName
                    )
                }.",
                relationView.patientGender,
                relation,
                expanded
            ) { update, value ->
                if (update) relation = value
                expanded = !expanded
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
                text = NameConverter.getFullName(
                    member.firstName,
                    member.middleName,
                    member.lastName
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${member.gender[0].uppercase()}/${
                    member.birthDate.toTimeInMilli().toAge()
                } · PID ${member.fhirId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = AddressConverter.getAddress(member.permanentAddress),
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
                    NameConverter.getFullName(
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
                            viewModel.patientFrom?.gender?.let {
                                RelationshipList.getRelationshipList(
                                    it
                                )
                            }
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
                        NameConverter.getFullName(
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