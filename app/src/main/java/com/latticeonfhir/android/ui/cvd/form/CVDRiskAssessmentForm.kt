package com.latticeonfhir.android.ui.cvd.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.YesNoEnum
import com.latticeonfhir.android.ui.cvd.CVDRiskAssessmentViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun CVDRiskAssessmentForm(
    viewModel: CVDRiskAssessmentViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.cvd_heading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DisplayField(
                stringResource(R.string.age),
                viewModel.patient?.birthDate?.toTimeInMilli()?.toAge().toString()
            )
            DisplayField(
                stringResource(R.string.gender_colon),
                viewModel.patient?.gender?.replaceFirstChar { it.uppercase() } ?: "")
            SegmentedButtonField(
                heading = stringResource(R.string.diabetic_colon),
                selectedValue = viewModel.isDiabetic,
                updateValue = { value ->
                    viewModel.isDiabetic = value
                }
            )
            SegmentedButtonField(
                heading = stringResource(R.string.current_smoker),
                selectedValue = viewModel.isSmoker,
                updateValue = { value ->
                    viewModel.isSmoker = value
                }
            )
            BloodPressureTextField(viewModel)
            CholesterolTextField(viewModel)
            HeightTextField(viewModel)
            WeightTextField(viewModel)
            BMIChip(viewModel)
        }
    }
}

@Composable
fun DisplayField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedButtonField(
    heading: String,
    selectedValue: String,
    updateValue: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(5f)
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.weight(5f)
        ) {
            YesNoEnum.listOfDisplay().forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                    onClick = { updateValue(label) },
                    selected = selectedValue == label
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun BloodPressureTextField(viewModel: CVDRiskAssessmentViewModel) {
    Column {
        Text(
            text = stringResource(R.string.blood_pressure_colon),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(7f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.systolic,
                    onValueChange = { value ->
                        if (value.matches(onlyNumbers) || value.isBlank())
                            viewModel.systolic = value
                    },
                    label = {
                        Text(stringResource(R.string.systolic))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = viewModel.diastolic,
                    onValueChange = { value ->
                        if (value.matches(onlyNumbers) || value.isBlank())
                            viewModel.diastolic = value
                    },
                    label = {
                        Text(stringResource(R.string.diastolic))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            Row(
                modifier = Modifier.weight(3f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = viewModel.bpUnit,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CholesterolTextField(viewModel: CVDRiskAssessmentViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(7f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.cholesterol,
                onValueChange = { value ->
                    if (value.matches(onlyNumbers) || value.isBlank())
                        viewModel.cholesterol = value
                },
                label = {
                    Text(stringResource(R.string.total_cholestrol))
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = viewModel.cholesterolUnits[viewModel.selectedCholesterolIndex],
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.swap),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (viewModel.selectedCholesterolIndex == 0) viewModel.selectedCholesterolIndex =
                        1
                    else viewModel.selectedCholesterolIndex = 0
                }
            )
        }
    }
}

@Composable
private fun HeightTextField(viewModel: CVDRiskAssessmentViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(7f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (viewModel.selectedHeightUnitIndex == 0) {
                OutlinedTextField(
                    value = viewModel.heightInCM,
                    onValueChange = { value ->
                        if (value.matches(onlyNumbers) || value.isBlank())
                            viewModel.heightInCM = value
                    },
                    label = {
                        Text(stringResource(R.string.height))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            } else {
                OutlinedTextField(
                    value = viewModel.heightInFeet,
                    onValueChange = { value ->
                        if (value.matches(onlyNumbers) || value.isBlank())
                            viewModel.heightInFeet = value
                    },
                    label = {
                        Text(stringResource(R.string.ft))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = viewModel.heightInInch,
                    onValueChange = { value ->
                        if (value.matches(onlyNumbers) || value.isBlank())
                            viewModel.heightInInch = value
                    },
                    label = {
                        Text(stringResource(R.string.inch))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = viewModel.heightUnits[viewModel.selectedHeightUnitIndex],
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.swap),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (viewModel.selectedHeightUnitIndex == 0) viewModel.selectedHeightUnitIndex =
                        1
                    else viewModel.selectedHeightUnitIndex = 0
                }
            )
        }
    }
}

@Composable
private fun WeightTextField(viewModel: CVDRiskAssessmentViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(7f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.weight,
                onValueChange = { value ->
                    if (value.matches(onlyNumbers) || value.isBlank())
                        viewModel.weight = value
                },
                label = {
                    Text(stringResource(R.string.weight))
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        Row(
            modifier = Modifier.weight(3f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = viewModel.weightUnit,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BMIChip(viewModel: CVDRiskAssessmentViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = if (viewModel.bmi.isBlank()) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = stringResource(R.string.bmi, viewModel.bmi.ifBlank { "--" }),
                style = if (viewModel.bmi.isBlank()) MaterialTheme.typography.bodyMedium
                else MaterialTheme.typography.labelLarge,
                color = if (viewModel.bmi.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
        if (viewModel.bmi.isBlank()) {
            Text(
                text = stringResource(R.string.bmi_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}