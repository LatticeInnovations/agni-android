package com.latticeonfhir.features.household.ui.suggestions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.ui.Loader
import com.latticeonfhir.core.ui.RelationDialogContent
import com.latticeonfhir.core.utils.converters.TimeConverter.toAge
import com.latticeonfhir.core.utils.converters.TimeConverter.toTimeInMilli
import com.latticeonfhir.core.utils.converters.responseconverter.AddressConverter
import com.latticeonfhir.core.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter
import com.latticeonfhir.features.household.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SuggestionsScreen(
    patient: PatientResponse,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    viewModel: SuggestionsScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.getQueueItems(patient)
    }
    if (viewModel.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Loader()
        }
    } else {
        if (viewModel.membersList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.no_suggested_members))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.nearby_patients_title),
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
                    text = "${member.gender[0].uppercase()}/${
                        member.birthDate.toTimeInMilli().toAge()
                    }",
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
                text = stringResource(id = R.string.connect_patient),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            RelationDialogContent(
                NameConverter.getFullName(
                    patient.firstName,
                    patient.middleName,
                    patient.lastName
                ),
                "of ${
                    NameConverter.getFullName(
                        member.firstName,
                        member.middleName,
                        member.lastName
                    )
                }.",
                patient.gender,
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
                            viewModel.getQueueItems(patient)
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
                    stringResource(id = R.string.create)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    stringResource(id = R.string.go_back)
                )
            }
        }
    )
}