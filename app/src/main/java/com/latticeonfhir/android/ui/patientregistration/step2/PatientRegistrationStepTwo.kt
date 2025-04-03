package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.common.IdLength
import com.latticeonfhir.android.ui.common.IdSelectionChip
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.theme.Neutral40

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
                text = stringResource(id = R.string.optional_identification),
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
                IdSelectionChip(
                    idSelected = viewModel.isPassportSelected,
                    label = stringResource(id = R.string.passport_id)
                ) {
                    viewModel.isPassportSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(
                    idSelected = viewModel.isVoterSelected,
                    label = stringResource(id = R.string.voter_id)
                ) {
                    viewModel.isVoterSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(
                    idSelected = viewModel.isPatientSelected,
                    label = stringResource(id = R.string.patient_id)
                ) {
                    viewModel.isPatientSelected = !it
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            PassportIdComposable(viewModel)
            VoterIdComposable(viewModel)
            PatientIdComposable(viewModel)
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

@Composable
private fun PatientIdComposable(viewModel: PatientRegistrationStepTwoViewModel) {
    Column {
        if (viewModel.isPatientSelected) {
            Spacer(modifier = Modifier.height(5.dp))
            CustomTextField(
                value = viewModel.patientId,
                label = stringResource(id = R.string.patient_id),
                weight = 1f,
                viewModel.maxPatientIdLength,
                viewModel.isPatientValid,
                stringResource(id = R.string.patient_id_error_msg),
                KeyboardType.Text,
                KeyboardCapitalization.Characters
            ) {
                viewModel.patientId = it
                viewModel.isPatientValid =
                    viewModel.patientId.length < viewModel.minPatientIdLength
            }
            IdLength(viewModel.patientId, viewModel.maxPatientIdLength, "PATIENT_ID_LENGTH")
        } else {
            viewModel.patientId = ""
            viewModel.isPatientValid = false
        }
    }
}

@Composable
private fun VoterIdComposable(viewModel: PatientRegistrationStepTwoViewModel) {
    Column {
        if (viewModel.isVoterSelected) {
            Spacer(modifier = Modifier.height(5.dp))
            CustomTextField(
                value = viewModel.voterId,
                label = stringResource(id = R.string.voter_id),
                weight = 1f,
                viewModel.maxVoterIdLength,
                viewModel.isVoterValid,
                stringResource(id = R.string.voter_id_error_msg),
                KeyboardType.Text,
                KeyboardCapitalization.Characters
            ) {
                viewModel.voterId = it
                viewModel.isVoterValid = !viewModel.voterPattern.matches(viewModel.voterId)
            }
            IdLength(viewModel.voterId, viewModel.maxVoterIdLength, "VOTER_ID_LENGTH")
        } else {
            viewModel.voterId = ""
            viewModel.isVoterValid = false
        }
    }
}

@Composable
private fun PassportIdComposable(viewModel: PatientRegistrationStepTwoViewModel) {
    Column {
        if (viewModel.isPassportSelected) {
            Spacer(modifier = Modifier.height(5.dp))
            CustomTextField(
                value = viewModel.passportId,
                label = stringResource(id = R.string.passport_id),
                weight = 1f,
                viewModel.maxPassportIdLength,
                viewModel.isPassportValid,
                stringResource(id = R.string.passport_id_error_msg),
                KeyboardType.Text,
                KeyboardCapitalization.Characters
            ) {
                viewModel.passportId = it
                viewModel.isPassportValid =
                    !viewModel.passportPattern.matches(viewModel.passportId)
            }
            IdLength(viewModel.passportId, viewModel.maxPassportIdLength, "PASSPORT_ID_LENGTH")
        } else {
            viewModel.passportId = ""
            viewModel.isPassportValid = false
        }
    }
}
