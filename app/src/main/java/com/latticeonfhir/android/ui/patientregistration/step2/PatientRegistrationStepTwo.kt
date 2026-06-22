package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.latticeonfhir.android.ui.common.CustomTextFieldWithLength
import com.latticeonfhir.android.ui.common.IdSelectionChip
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.ui.theme.Neutral40

@Composable
fun PatientRegistrationStepTwo(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepTwoViewModel = viewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            setData(patientRegister, viewModel, patientRegistrationViewModel)
            viewModel.isLaunched = true
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IdSelectionChip(
                    idSelected = viewModel.isAbhaSelected,
                    label = stringResource(id = R.string.abha_id)
                ) {
                    viewModel.isAbhaSelected = !it
                    if (!viewModel.isAbhaSelected) {
                        viewModel.abhaId = ""
                        viewModel.isAbhaIdValid = false
                    }
                }
                IdSelectionChip(
                    idSelected = viewModel.isRationCardSelected,
                    label = stringResource(id = R.string.ration_card)
                ) {
                    viewModel.isRationCardSelected = !it
                    if (!viewModel.isRationCardSelected) {
                        viewModel.rationCard = ""
                        viewModel.isRationCardValid = false
                    }
                }
            }
            AbhaIdComposable(viewModel)
            RationCardComposable(viewModel)
        }
        Button(
            onClick = {
                patientRegister.run {
                    abhaId = viewModel.abhaId
                    rationCard = viewModel.rationCard
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

private fun setData(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepTwoViewModel,
    patientRegistrationViewModel: PatientRegistrationViewModel
) {
    patientRegister.run {
        viewModel.abhaId = abhaId.toString()
        viewModel.rationCard = rationCard.toString()
        if (patientRegistrationViewModel.isEditing) {
            viewModel.isAbhaSelected = abhaId.toString().isNotEmpty()
            viewModel.isRationCardSelected = rationCard.toString().isNotEmpty()
        }
    }
}

@Composable
private fun AbhaIdComposable(viewModel: PatientRegistrationStepTwoViewModel) {
    if (viewModel.isAbhaSelected) {
        OutlinedTextField(
            value = viewModel.abhaId,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(14)

                viewModel.abhaId = digits
                viewModel.isAbhaIdValid = digits.length != 14
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = stringResource(R.string.abha_id))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = AbhaIdVisualTransformation(),
            isError = viewModel.isAbhaIdValid,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.isAbhaIdValid) {
                        Text(stringResource(R.string.abha_id_error_msg))
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${viewModel.abhaId.length}/${viewModel.maxAbhaIdLength}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun RationCardComposable(viewModel: PatientRegistrationStepTwoViewModel) {
    if (viewModel.isRationCardSelected) {
        CustomTextFieldWithLength(
            value = viewModel.rationCard,
            label = stringResource(id = R.string.ration_card),
            weight = 1f,
            maxLength = viewModel.maxRationCardLength,
            isError = viewModel.isRationCardValid,
            error = stringResource(id = R.string.ration_card_error_msg),
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Characters
        ) { input ->
            val value = input.uppercase()
                .filter { it.isLetterOrDigit() }
                .take(viewModel.maxRationCardLength)

            viewModel.rationCard = value
            viewModel.isRationCardValid = !viewModel.rationCardRegex.matches(value)
        }
    }
}
