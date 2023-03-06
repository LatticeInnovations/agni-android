package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.ui.theme.Neutral10
import com.latticeonfhir.android.ui.main.ui.theme.Neutral40
import com.latticeonfhir.android.ui.main.ui.theme.Primary40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationStepTwo(viewModel: PatientRegistrationViewModel) {
    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Identification",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 2/3",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IdSelectionChip(idSelected = viewModel.isPassportSelected, label = "Passport Id") {
                    viewModel.isPassportSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(idSelected = viewModel.isVoterSelected, label = "Voter Id") {
                    viewModel.isVoterSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(idSelected = viewModel.isPatientSelected, label = "Patient Id") {
                    viewModel.isPatientSelected = !it
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (viewModel.isPassportSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.passportId,
                    label = "Passport Id",
                    weight = 1f,
                    viewModel.maxPassportIdLength
                ) {
                    viewModel.passportId = it
                }
                IdLength(viewModel.passportId, viewModel.maxPassportIdLength)
            }
            if (viewModel.isVoterSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.voterId,
                    label = "Voter Id",
                    weight = 1f,
                    viewModel.maxVoterIdLength
                ) {
                    viewModel.voterId = it
                }
                IdLength(viewModel.voterId, viewModel.maxVoterIdLength)
            }
            if (viewModel.isPatientSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.patientId,
                    label = "Patient Id",
                    weight = 1f,
                    viewModel.maxPatientIdLength
                ) {
                    viewModel.patientId = it
                }
                IdLength(viewModel.patientId, viewModel.maxPatientIdLength)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.step = 3
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = viewModel.identityInfoValidation()
        ) {
            Text(text = "Next")
        }
    }
}

@Composable
fun IdSelectionChip(idSelected: Boolean, label: String, updateSelection: (Boolean) -> Unit) {
    AssistChip(
        onClick = {
            updateSelection(idSelected)
        },
        label = { Text(text = label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (idSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.background,
            labelColor = if (idSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        ),
        leadingIcon = {
            if (idSelected)
                Icon(Icons.Default.Check, contentDescription = null, tint = Primary40)
        }
    )
}

@Composable
fun IdLength(idName: String, requiredLength: Int) {
    Text(
        text = "${idName.length}/$requiredLength",
        textAlign = TextAlign.Right,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, end = 15.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}