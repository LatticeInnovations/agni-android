package com.latticeonfhir.android.ui.main.searchpatient

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.common.CustomFilterChip
import com.latticeonfhir.android.ui.main.common.CustomTextField

@Composable
fun SearchPatientForm(searchPatientViewModel: SearchPatientViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = "Search using any of the field below",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = searchPatientViewModel.patientName,
                label = "Patient Name",
                weight = 1f,
                maxLength = 150, searchPatientViewModel.isNameValid,
                "Name length should be between 3 and 150."
            ) {
                searchPatientViewModel.patientName = it
                searchPatientViewModel.isNameValid =
                    searchPatientViewModel.patientName.length < 3 || searchPatientViewModel.patientName.length > 150
            }
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = searchPatientViewModel.patientId,
                label = "Patient Id",
                weight = 1f,
                maxLength = 150, searchPatientViewModel.isPatientIdValid,
                "Enter valid Patient Id."
            ) {
                searchPatientViewModel.patientId = it
                searchPatientViewModel.isPatientIdValid = searchPatientViewModel.patientId.length < 10
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Select age range",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AgeBox(searchPatientViewModel.minAge, "Min") {
                    searchPatientViewModel.minAge = it
                }
                AgeBox(searchPatientViewModel.maxAge, "Max") {
                    searchPatientViewModel.maxAge = it
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            AgeRangeSlider(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            GenderComposable(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            VisitDropdown(searchPatientViewModel)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { searchPatientViewModel.step = 2 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next", style = MaterialTheme.typography.labelLarge)
        }
    }
}


@Composable
fun AgeBox(age: String, label: String, updateAge: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = age,
            onValueChange = {
                updateAge(it)
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(75.dp)
                .padding(end = 5.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            readOnly = true
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun AgeRangeSlider(viewModel: SearchPatientViewModel){
    RangeSlider(
        modifier = Modifier.testTag("age range slider"),
        value = viewModel.range,
        onValueChange = {
            viewModel.range = it
            viewModel.minAge = it.start.toInt().toString()
            viewModel.maxAge = it.endInclusive.toInt().toString()
        },
        valueRange = 0f..100f
    )
}

@Composable
fun GenderComposable(viewModel: SearchPatientViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Gender", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = "male",
            label = "Male"
        ){
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = "female",
            label = "Female"
        ){
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = "others",
            label = "Others"
        ){
            viewModel.gender = it
        }
    }
}

@Composable
fun VisitDropdown(viewModel: SearchPatientViewModel) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = viewModel.visitSelected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("last facility visit"),
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
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "")
            },
            label = {
                Text(text = "Last facility visit")
            }
        )
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(0.9f),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            viewModel.visitIntervals.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        viewModel.visitSelected = label
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