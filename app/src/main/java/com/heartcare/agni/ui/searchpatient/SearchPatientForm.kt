package com.heartcare.agni.ui.searchpatient

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.enums.LastVisit.Companion.getLastVisitList
import com.heartcare.agni.data.local.enums.RiskCategoryEnum.Companion.getRiskCategoryList
import com.heartcare.agni.ui.common.CustomFilterChip
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.patientregistration.step3.DropDownComposable

@Composable
fun SearchPatientForm(
    viewModel: SearchPatientViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .testTag("ROOT_LAYOUT"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.search_helper_text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            PatientNameComposable(viewModel)
            RiskCategoryComposable(viewModel)
            CustomTextField(
                value = viewModel.heartcareId,
                label = stringResource(R.string.heartcare_id),
                weight = 1f,
                maxLength = viewModel.maxHeartcareIdLength,
                isError = false,
                error = "",
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.None,
                updateValue = {
                    viewModel.heartcareId = it
                }
            )
            CustomTextField(
                value = viewModel.hospitalId,
                label = stringResource(R.string.hospital_id),
                weight = 1f,
                maxLength = viewModel.maxHospitalIdLength,
                isError = false,
                error = "",
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.None,
                updateValue = {
                    viewModel.hospitalId = it
                }
            )
            CustomTextField(
                value = viewModel.nationalId,
                label = stringResource(R.string.national_id),
                weight = 1f,
                maxLength = viewModel.maxNationalIdLength,
                isError = false,
                error = "",
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.None,
                updateValue = {
                    viewModel.nationalId = it
                }
            )
            DropDownComposable(
                value = viewModel.province.name,
                updateValue = {
                    viewModel.province = it
                    viewModel.areaCouncil = viewModel.select
                    viewModel.getAreaCouncilList()
                },
                label = stringResource(id = R.string.province),
                dropdownList = viewModel.provinceList,
                errorText = stringResource(R.string.province_required),
                isMandatory = false,
                isEnabled = true
            )
            DropDownComposable(
                value = viewModel.areaCouncil.name,
                updateValue = {
                    viewModel.areaCouncil = it
                },
                label = stringResource(id = R.string.area_council),
                dropdownList = viewModel.areaCouncilList,
                errorText = stringResource(R.string.area_council_required),
                isMandatory = false,
                isEnabled = true
            )
            Text(
                text = stringResource(id = R.string.select_age_range),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AgeBoxRow(viewModel)
            AgeRangeSlider(viewModel)
            GenderComposable(viewModel)
            VisitDropdown(viewModel)
            Spacer(Modifier.height(64.dp))
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
private fun PatientNameComposable(viewModel: SearchPatientViewModel) {
    CustomTextField(
        value = viewModel.patientName,
        label = stringResource(id = R.string.patient_name),
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        viewModel.patientName = it
    }
}


@Composable
private fun AgeBox(age: String, label: String, updateAge: (String) -> Unit) {
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
private fun AgeRangeSlider(viewModel: SearchPatientViewModel) {
    RangeSlider(
        value = viewModel.range,
        onValueChange = {
            viewModel.range = it
            viewModel.minAge = it.start.toInt().toString()
            viewModel.maxAge = it.endInclusive.toInt().toString()
        }
    )
}

@Composable
private fun GenderComposable(viewModel: SearchPatientViewModel) {
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
    }
}

@Composable
private fun VisitDropdown(viewModel: SearchPatientViewModel) {
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

@Composable
private fun RiskCategoryComposable(
    viewModel: SearchPatientViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.risk_category),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            getRiskCategoryList().forEach { riskCategory ->
                CustomFilterChip(
                    selector = viewModel.riskCategory,
                    selected = riskCategory,
                    label = riskCategory,
                    updateSelected = { updatedRisk ->
                        if (viewModel.riskCategory == updatedRisk) viewModel.riskCategory = ""
                        else viewModel.riskCategory = updatedRisk
                    }
                )
            }
        }
    }
}