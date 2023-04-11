package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister

@Composable
fun PatientRegistrationStepTwo(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepTwoViewModel = viewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.patientId = patientId.toString()
                viewModel.passportId = passportId.toString()
                viewModel.voterId = voterId.toString()
                if (patientRegistrationViewModel.isEditing) viewModel.isPassportSelected =
                    passportId.toString().isNotEmpty()
                viewModel.isVoterSelected = voterId.toString().isNotEmpty()
                viewModel.isPatientSelected = patientId.toString().isNotEmpty()
            }
        }
    }
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 2/${patientRegistrationViewModel.totalSteps}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
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
                    viewModel.maxPassportIdLength,
                    viewModel.isPassportValid,
                    "Enter valid Passport ID (eg., A1098765)"
                ) {
                    viewModel.passportId = it
                    viewModel.isPassportValid =
                        !viewModel.passportPattern.matches(viewModel.passportId)
                }
                IdLength(viewModel.passportId, viewModel.maxPassportIdLength)
            }
            if (viewModel.isVoterSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.voterId,
                    label = "Voter Id",
                    weight = 1f,
                    viewModel.maxVoterIdLength,
                    viewModel.isVoterValid,
                    "Enter valid Voter Id (eg., XYZ9876543)"
                ) {
                    viewModel.voterId = it
                    viewModel.isVoterValid = !viewModel.voterPattern.matches(viewModel.voterId)
                }
                IdLength(viewModel.voterId, viewModel.maxVoterIdLength)
            }
            if (viewModel.isPatientSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.patientId,
                    label = "Patient Id",
                    weight = 1f,
                    viewModel.maxPatientIdLength,
                    viewModel.isPatientValid,
                    "Enter valid Patient Id"
                ) {
                    viewModel.patientId = it
                    viewModel.isPatientValid = viewModel.patientId.length < 10
                }
                IdLength(viewModel.patientId, viewModel.maxPatientIdLength)
            }
        }
        Button(
            onClick = {
                patientRegister.run {
                    passportId = viewModel.passportId
                    voterId = viewModel.voterId
                    patientId = viewModel.patientId
                }
                patientRegistrationViewModel.currentStep = 3
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = viewModel.identityInfoValidation()
        ) {
            Text(text = "Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdSelectionChip(idSelected: Boolean, label: String, updateSelection: (Boolean) -> Unit) {
    FilterChip(
        selected = idSelected,
        onClick = {
            updateSelection(idSelected)
        },
        label = { Text(text = label) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            selectedBorderWidth = 1.dp
        ),
        leadingIcon = {
            if (idSelected)
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
        },
        modifier = Modifier.testTag("$label chip")
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