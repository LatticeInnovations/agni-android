package com.latticeonfhir.android.ui.searchpatient

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
import com.latticeonfhir.android.ui.common.AddressComposable
import com.latticeonfhir.android.ui.common.CustomFilterChip
import com.latticeonfhir.android.ui.common.CustomTextField

@Composable
fun SearchPatientForm(searchPatientViewModel: SearchPatientViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp).testTag("ROOT_LAYOUT")
        ) {
            Text(
                text = "Search using any of the field below",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(15.dp))
            GenderComposable(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = searchPatientViewModel.patientName,
                label = "Patient Name",
                weight = 1f,
                maxLength = 100, searchPatientViewModel.isNameValid,
                "Name length should be between 3 and 100.",
                KeyboardType.Text
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
                maxLength = 10, searchPatientViewModel.isPatientIdValid,
                "Patient Id should be of length 10.",
                KeyboardType.Text
            ) {
                searchPatientViewModel.patientId = it
                searchPatientViewModel.isPatientIdValid =
                    searchPatientViewModel.patientId.length < 10
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Select age range",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AgeBox(searchPatientViewModel.minAge, "Min") {
                    if (it.length == 0) searchPatientViewModel.minAge = it
                    else if (it.matches(searchPatientViewModel.onlyNumbers)) {
                        if (it.toInt()>=0 && it.toInt()<=100) searchPatientViewModel.minAge = it
                    }
                    searchPatientViewModel.updateRange(
                        searchPatientViewModel.minAge,
                        searchPatientViewModel.maxAge
                    )
                }
                AgeBox(searchPatientViewModel.maxAge, "Max") {
                    if (it.length == 0) searchPatientViewModel.maxAge = it
                    else if (it.matches(searchPatientViewModel.onlyNumbers)) {
                        if (it.toInt()>=0 && it.toInt()<=100) searchPatientViewModel.maxAge = it
                    }
                    searchPatientViewModel.updateRange(
                        searchPatientViewModel.minAge,
                        searchPatientViewModel.maxAge
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            AgeRangeSlider(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            VisitDropdown(searchPatientViewModel)
            Spacer(modifier = Modifier.height(30.dp))
            AddressComposable(label = "Address", address = searchPatientViewModel.address) {}
            Spacer(modifier = Modifier.height(50.dp).testTag("END_OF_SCREEN"))
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
                .padding(end = 5.dp)
                .testTag("${label.uppercase()}_VALUE"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun AgeRangeSlider(viewModel: SearchPatientViewModel) {
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
        ) {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = "female",
            label = "Female"
        ) {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = "others",
            label = "Others"
        ) {
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