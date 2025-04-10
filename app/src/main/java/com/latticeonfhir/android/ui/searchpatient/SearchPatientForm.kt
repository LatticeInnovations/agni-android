package com.latticeonfhir.core.ui.searchpatient

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.core.data.local.enums.GenderEnum
import com.latticeonfhir.core.data.local.enums.LastVisit.Companion.getLastVisitList
import com.latticeonfhir.core.ui.common.AddressComposable
import com.latticeonfhir.core.ui.common.CustomFilterChip
import com.latticeonfhir.core.ui.common.CustomTextField

@Composable
fun SearchPatientForm(searchPatientViewModel: SearchPatientViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .testTag("ROOT_LAYOUT")
        ) {
            Text(
                text = stringResource(id = R.string.search_helper_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(15.dp))
            GenderComposable(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            PatientNameComposable(searchPatientViewModel)
            Spacer(modifier = Modifier.height(15.dp))
            PatientIdComposable(searchPatientViewModel)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.select_age_range),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(15.dp))
            AgeBoxRow(searchPatientViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            AgeRangeSlider(viewModel = searchPatientViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            VisitDropdown(searchPatientViewModel)
            Spacer(modifier = Modifier.height(30.dp))
            AddressComposable(
                label = "Address",
                address = searchPatientViewModel.address,
                isSearching = true
            )
            Spacer(
                modifier = Modifier
                    .height(50.dp)
                    .testTag("END_OF_SCREEN")
            )
        }
    }

}

@Composable
private fun AgeBoxRow(searchPatientViewModel: SearchPatientViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AgeBox(searchPatientViewModel.minAge, "Min") {
            if (it.isEmpty()) searchPatientViewModel.minAge = it
            else if (it.matches(searchPatientViewModel.onlyNumbers) && it.toInt() in 0..100)
                searchPatientViewModel.minAge = it
            searchPatientViewModel.updateRange(
                searchPatientViewModel.minAge,
                searchPatientViewModel.maxAge
            )
        }
        AgeBox(searchPatientViewModel.maxAge, "Max") {
            if (it.isEmpty()) searchPatientViewModel.maxAge = it
            else if (it.matches(searchPatientViewModel.onlyNumbers) && it.toInt() in 0..100)
                searchPatientViewModel.maxAge = it
            searchPatientViewModel.updateRange(
                searchPatientViewModel.minAge,
                searchPatientViewModel.maxAge
            )
        }
    }
}

@Composable
private fun PatientIdComposable(searchPatientViewModel: SearchPatientViewModel) {
    CustomTextField(
        value = searchPatientViewModel.patientId,
        label = stringResource(id = R.string.patient_id),
        weight = 1f,
        maxLength = 12, searchPatientViewModel.isPatientIdValid,
        stringResource(id = R.string.patient_id_error_msg),
        KeyboardType.Text,
        KeyboardCapitalization.Characters
    ) {
        searchPatientViewModel.patientId = it
        searchPatientViewModel.isPatientIdValid =
            searchPatientViewModel.patientId.isNotEmpty() && searchPatientViewModel.patientId.length < 2
    }
}

@Composable
private fun PatientNameComposable(searchPatientViewModel: SearchPatientViewModel) {
    CustomTextField(
        value = searchPatientViewModel.patientName,
        label = stringResource(id = R.string.patient_name),
        weight = 1f,
        maxLength = 100, searchPatientViewModel.isNameValid,
        stringResource(id = R.string.patient_name_error_msg),
        KeyboardType.Text,
        KeyboardCapitalization.Words
    ) {
        if (it.length <= 150) searchPatientViewModel.patientName = it
        searchPatientViewModel.isNameValid =
            searchPatientViewModel.patientName.isNotEmpty() && searchPatientViewModel.patientName.length < 3
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
        Text(
            text = stringResource(id = R.string.gender),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = GenderEnum.MALE.value,
            label = stringResource(id = R.string.male)
        ) {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = GenderEnum.FEMALE.value,
            label = stringResource(id = R.string.female)
        ) {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            selector = viewModel.gender,
            selected = GenderEnum.OTHER.value,
            label = stringResource(id = R.string.other)
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
                Text(text = stringResource(id = R.string.last_facility_visit))
            }
        )
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(0.9f),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            getLastVisitList().forEach { label ->
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