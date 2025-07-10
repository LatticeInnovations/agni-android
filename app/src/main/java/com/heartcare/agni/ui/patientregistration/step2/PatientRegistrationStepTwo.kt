package com.heartcare.agni.ui.patientregistration.step2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.NationalIdUse
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import com.heartcare.agni.utils.regex.RegexPatterns.atLeastOneAlphaAndNumber

@Composable
fun PatientRegistrationStepTwo(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepTwoViewModel = viewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.hospitalId = hospitalId.toString()
                viewModel.nationalId = nationalId.toString()
                viewModel.isNationalIdVerified = nationalIdUse == NationalIdUse.OFFICIAL.use
                viewModel.isVerifyClicked = !nationalIdUse.isNullOrBlank()
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
                text = stringResource(id = R.string.identification),
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HospitalIdComposable(viewModel)
            NationalIdComposable(viewModel)
        }
        Button(
            onClick = {
                patientRegister.run {
                    nationalId = viewModel.nationalId
                    hospitalId = viewModel.hospitalId
                    nationalIdUse = if (viewModel.nationalId.isNotBlank()) {
                        if (viewModel.isNationalIdVerified) NationalIdUse.OFFICIAL.use
                        else NationalIdUse.TEMP.use
                    } else null
                }
                patientRegistrationViewModel.currentStep = 3
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = viewModel.identityInfoValidation()
        ) {
            Text(text = stringResource(R.string.next))
        }
    }
}

@Composable
private fun HospitalIdComposable(
    viewModel: PatientRegistrationStepTwoViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.hospitalId,
        label = stringResource(id = R.string.hospital_id),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxHospitalIdLength,
        isError = viewModel.isHospitalIdValid,
        error = stringResource(id = R.string.hospital_id_error_msg),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Characters
    ) { value ->
        val filtered = value.filter { it.isLetterOrDigit() }
        if (value.length <= viewModel.maxHospitalIdLength && (value == filtered || value.isEmpty()))
            viewModel.hospitalId = value
        viewModel.isHospitalIdValid = viewModel.hospitalId.isNotBlank() &&
                !atLeastOneAlphaAndNumber.matches(viewModel.hospitalId)
    }
}

@Composable
private fun NationalIdComposable(
    viewModel: PatientRegistrationStepTwoViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = viewModel.nationalId,
            onValueChange = {
                if (it.length <= viewModel.maxNationalIdLength && (it.matches(onlyNumbers) || it.isEmpty())) {
                    viewModel.nationalId = it
                    viewModel.isVerifyClicked = false
                    viewModel.isNationalIdVerified = false
                }
            },
            label = {
                Text(stringResource(R.string.national_id))
            },
            modifier = Modifier.weight(3f),
            supportingText = {
                NationalIdSupportingText(viewModel)
            },
            trailingIcon = {
                if (viewModel.nationalId.isNotBlank())
                    IconButton(
                        onClick = {
                            viewModel.nationalId = ""
                            viewModel.isVerifyClicked = false
                            viewModel.isNationalIdVerified = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cancel),
                            contentDescription = null
                        )
                    }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Button(
            onClick = {
                viewModel.isVerifyClicked = true
                // TODO : add logic to verify national id
                viewModel.isNationalIdVerified = true
            },
            modifier = Modifier.weight(1f),
            enabled = viewModel.nationalId.isNotBlank() && !viewModel.isNationalIdVerified
        ) {
            Text(stringResource(R.string.verify))
        }
    }
}

@Composable
private fun NationalIdSupportingText(
    viewModel: PatientRegistrationStepTwoViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.isVerifyClicked) {
            val text: String
            val icon: Int
            val color: Color
            if (viewModel.isNationalIdVerified) {
                text = stringResource(R.string.verified)
                icon = R.drawable.sync_completed_icon
                color = MaterialTheme.colorScheme.primary
            } else {
                text = stringResource(R.string.unverified)
                icon = R.drawable.info
                color = MaterialTheme.colorScheme.onSurfaceVariant
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${viewModel.nationalId.length}/${viewModel.maxNationalIdLength}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}