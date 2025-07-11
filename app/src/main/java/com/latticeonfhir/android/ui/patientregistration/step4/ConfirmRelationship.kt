package com.latticeonfhir.android.ui.patientregistration.step4

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.DiscardAllRelationDialog
import com.latticeonfhir.android.ui.theme.Neutral40
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationEnumFromString
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationFromRelationEnum
import com.latticeonfhir.android.utils.converters.responseconverter.RelationshipList
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRelationship(
    navController: NavController,
    viewModel: ConfirmRelationshipViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.relation =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                    key = "relation"
                )!!
            viewModel.patientId =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                    key = "patientId"
                )!!
            viewModel.relativeId =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                    key = "relativeId"
                )!!
            viewModel.getPatientData(viewModel.patientId) {
                viewModel.patient = it
            }
            viewModel.getPatientData(viewModel.relativeId) {
                viewModel.relative = it
            }
            viewModel.getRelationBetween(viewModel.patientId, viewModel.relativeId)
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.confirm_relationship),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
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
//                            contentDescription = "BACK_ICON"
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
                ConfirmRelationshipScreen(context, navController, viewModel)
                if (viewModel.discardAllRelationDialog) {
                    DiscardAllRelationDialog { discard ->
                        if (discard) {
                            viewModel.discardRelations()
                            navController.popBackStack(
                                Screen.HouseholdMembersScreen.route,
                                false
                            )
                        }
                        viewModel.discardAllRelationDialog = false
                    }
                }
            }
        }
    )
}

@Composable
fun ConfirmRelationshipScreen(
    context: Context,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.relationBetween) { relationView ->
                MemberCard(context, relationView, viewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        Button(
            onClick = {
                viewModel.addRelationsToGenericEntity()
                navController.popBackStack(Screen.HouseholdMembersScreen.route, false)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .testTag("CONNECT_BTN")
        ) {
            Text(text = "Connect Patient")
        }

    }
}

@Composable
fun MemberCard(
    context: Context,
    relationView: RelationView,
    viewModel: ConfirmRelationshipViewModel
) {
    var openDeleteDialog by remember {
        mutableStateOf(false)
    }
    var openEditDialog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .padding(14.dp)
            .testTag("MEMBER_DETAIL_CARDS")
    ) {
        Text(
            text = "${
                NameConverter.getFullName(
                    relationView.patientFirstName,
                    relationView.patientMiddleName,
                    relationView.patientLastName
                )
            } " +
                    "is the ${getRelationFromRelationEnum(context, relationView.relation)} of " +
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
    viewModel: ConfirmRelationshipViewModel,
    closeDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Remove relationship?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("DIALOG_TITLE")
            )
        },
        text = {
            Column {
                Text(
                    "Are you sure you want to remove this relationship? Patient records will not be affected.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("DIALOG_SUBTITLE")
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
                                getRelationFromRelationEnum(
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
                    viewModel.deleteRelation(relationView.patientId, relationView.relativeId)
                    closeDialog()
                },
                modifier = Modifier.testTag("POSITIVE_BTN")
            ) {
                Text(
                    "Confirm"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                },
                modifier = Modifier.testTag("NEGATIVE_BTN")
            ) {
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
    viewModel: ConfirmRelationshipViewModel,
    closeDialog: () -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var relation by remember {
        mutableStateOf(
            getRelationFromRelationEnum(
                context,
                relationView.relation
            ).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            })
    }
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Edit relationship",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("DIALOG_TITLE")
            )
        },
        text = {
            Column {
                Text(
                    NameConverter.getFullName(
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
                    Column(
                        modifier = Modifier.testTag("RELATIONS_DROPDOWN")
                    ) {
                        val relationsList =
                            RelationshipList.getRelationshipList(relationView.patientGender)

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
                        NameConverter.getFullName(
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
                    viewModel.relation = relation
                    viewModel.updateRelation(
                        Relation(
                            patientId = relationView.patientId,
                            relation = getRelationEnumFromString(relation),
                            relativeId = relationView.relativeId
                        )
                    )
                    closeDialog()
                },
                modifier = Modifier.testTag("POSITIVE_BTN")
            ) {
                Text(
                    "Save"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                },
                modifier = Modifier.testTag("NEGATIVE_BTN")
            ) {
                Text(
                    "Cancel"
                )
            }
        }
    )
}