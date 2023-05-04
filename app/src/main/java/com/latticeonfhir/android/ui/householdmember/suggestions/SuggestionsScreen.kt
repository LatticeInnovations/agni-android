package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.householdmember.suggestions.SuggestionsScreenViewModel
import com.latticeonfhir.android.utils.constants.RelationshipList
import com.latticeonfhir.android.utils.converters.responseconverter.AddressConverter
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.relation.RelationConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SuggestionsScreen(
    patient: PatientResponse,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    viewModel: SuggestionsScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.getQueueItems(patient)
        }
        viewModel.isLaunched = true
    }
    if (viewModel.loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Loader()
        }
    }
    else{
        if (viewModel.membersList.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "No suggested members.")
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Here are patients with similar addresses or nearby locations.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(viewModel.membersList) { member ->
                        SuggestedMembersCard(scope, snackbarHostState, viewModel, member, patient)
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestedMembersCard(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: SuggestionsScreenViewModel,
    member: PatientResponse,
    patient: PatientResponse
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
                    text = "${member.gender[0].uppercase()}/${member.birthDate.toTimeInMilli().toAge()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = AddressConverter.getAddress(member.permanentAddress),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "+91-${member.mobileNumber} · PID ${member.fhirId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (showConnectDialog) {
        ConnectDialog(snackbarHostState, scope, member, patient, viewModel) {
            showConnectDialog = false
        }
    }
}

@Composable
fun ConnectDialog(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    member: PatientResponse,
    patient: PatientResponse,
    viewModel: SuggestionsScreenViewModel,
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
                    NameConverter.getFullName(patient.firstName, patient.middleName, patient.lastName),
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
                            RelationshipList.getRelationshipList(patient.gender)
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
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(0.4f),
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
                            patientId = patient.id,
                            relativeId = member.id,
                            relation = RelationConverter.getRelationEnumFromString(relation)
                        ),
                        member.id
                    ) {
                        if (it.isNotEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${
                                        NameConverter.getFullName(
                                            member.firstName,
                                            member.middleName,
                                            member.lastName
                                        )
                                    } added to the household.",
                                    withDismissAction = true
                                )
                            }
                            viewModel.updateQueue(member)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Relation not added.",
                                    withDismissAction = true
                                )
                            }
                        }
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