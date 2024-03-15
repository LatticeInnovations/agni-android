package com.latticeonfhir.android.ui.householdmember.suggestions

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
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.ui.common.RelationDialogContent
import com.latticeonfhir.android.utils.converters.responseconverter.AddressConverter.getAddressFhir
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Patient

@Composable
fun SuggestionsScreen(
    patient: Patient,
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
    member: Patient,
    patient: Patient
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
                    text = member.nameFirstRep.nameAsSingleString,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${member.gender.display[0].uppercase()}/${
                        member.birthDate.time.toAge()
                    }",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = getAddressFhir(member.addressFirstRep),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "+91-${member.telecom.first { it.system == ContactPoint.ContactPointSystem.PHONE }.value} · PID ${member.logicalId}",
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
    member: Patient,
    patient: Patient,
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
                patient.nameFirstRep.nameAsSingleString,
                "of ${
                    member.nameFirstRep.nameAsSingleString
                }.",
                patient.gender.toCode(),
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
                        patient,
                        member,
                        RelationConverter.getRelationEnumFromString(relation)
                    ) {
                        viewModel.getQueueItems(patient)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${
                                    member.nameFirstRep.nameAsSingleString
                                } added to the household.",
                                withDismissAction = true
                            )
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