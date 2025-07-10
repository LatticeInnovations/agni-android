package com.heartcare.agni.ui.patientregistration.step1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.DeceasedReason
import com.heartcare.agni.data.local.enums.DeceasedReason.Companion.getDeceasedReasonList
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.ui.common.CustomFilterChip
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import com.heartcare.agni.utils.converters.responseconverter.MonthsList.getMonthsList
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isDOBValid
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.regex.NameRegex.nameRegex
import com.heartcare.agni.utils.regex.PhoneNumberRegex.phoneNumberRegex

@Composable
fun PatientRegistrationStepOne(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepOneViewModel = viewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.firstName = firstName.toString()
                viewModel.lastName = lastName.toString()
                viewModel.phoneNumber = phoneNumber.toString()
                viewModel.dobAgeSelector = dobAgeSelector.toString()
                viewModel.dobDay = dobDay.toString()
                viewModel.dobMonth = dobMonth.toString()
                viewModel.dobYear = dobYear.toString()
                viewModel.years = years.toString()
                viewModel.months = months.toString()
                viewModel.days = days.toString()
                viewModel.gender = gender.toString()
                viewModel.isPersonDeceased = isPersonDeceased ?: 0
                viewModel.selectedDeceasedReason = personDeceasedReason.toString()
                viewModel.motherName = motherName.toString()
                viewModel.fatherName = fatherName.toString()
                viewModel.spouseName = spouseName.toString()
            }
            viewModel.isLaunched = true
        }
    }
    Column(
        modifier = Modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.basic_information),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 1/${patientRegistrationViewModel.totalSteps}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .testTag("columnLayout")
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CustomTextFieldWithLength(
                value = viewModel.lastName,
                label = stringResource(id = R.string.last_name_mandatory),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxNameLength,
                isError = viewModel.isLastNameValid,
                error = stringResource(R.string.last_name_required),
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.lastName = it.trim()
                viewModel.isLastNameValid = viewModel.lastName.isBlank()
            }
            CustomTextFieldWithLength(
                value = viewModel.firstName,
                label = stringResource(id = R.string.first_name_mandatory),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxNameLength,
                isError = viewModel.isFirstNameValid,
                error = stringResource(id = R.string.first_name_required),
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.firstName = it.trim()
                viewModel.isFirstNameValid = viewModel.firstName.isBlank()
            }
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CustomFilterChip(viewModel.dobAgeSelector, "dob", "Date of Birth") {
                        viewModel.dobAgeSelector = it
                        viewModel.days = ""
                        viewModel.months = ""
                        viewModel.years = ""
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomFilterChip(viewModel.dobAgeSelector, "age", "Age") {
                        viewModel.dobAgeSelector = it
                        viewModel.dobDay = ""
                        viewModel.dobMonth = ""
                        viewModel.dobYear = ""
                    }
                }
                if (viewModel.dobAgeSelector == "dob") {
                    DobTextField(viewModel)
                } else
                    AgeTextField(viewModel)
            }
            CustomTextFieldWithLength(
                value = viewModel.phoneNumber,
                label = stringResource(id = R.string.mobile),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxPhoneNumberLength,
                isError = viewModel.isPhoneValid,
                error = stringResource(id = R.string.phone_number_error_msg),
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.length <= viewModel.maxPhoneNumberLength && (it.matches(viewModel.onlyNumbers) || it.isEmpty()))
                    viewModel.phoneNumber = it
                viewModel.isPhoneValid =
                    viewModel.phoneNumber.isNotBlank() && !viewModel.phoneNumber.matches(
                        phoneNumberRegex
                    )
            }

            GenderComposable(viewModel)
            PatientDeceasedComposable(viewModel)
            CustomTextFieldWithLength(
                value = viewModel.motherName,
                label = stringResource(id = R.string.mother_name_mandatory),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxNameLength,
                isError = viewModel.isMotherNameValid,
                error = stringResource(id = R.string.mother_name_required),
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.motherName = it.trim()
                viewModel.isMotherNameValid = viewModel.motherName.isBlank()
            }

            CustomTextFieldWithLength(
                value = viewModel.fatherName,
                label = stringResource(id = R.string.father_name),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxNameLength,
                isError = false,
                error = "",
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.fatherName = it.trim()
            }

            CustomTextFieldWithLength(
                value = viewModel.spouseName,
                label = stringResource(id = R.string.spouse_name),
                placeholder = null,
                weight = 1f,
                maxLength = viewModel.maxNameLength,
                isError = false,
                error = "",
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.spouseName = it.trim()
            }
        }
        Button(
            onClick = {
                patientRegister.run {
                    firstName = viewModel.firstName.capitalizeFirst()
                    lastName = viewModel.lastName.capitalizeFirst()
                    dobAgeSelector = viewModel.dobAgeSelector
                    dobDay = viewModel.dobDay
                    dobMonth = viewModel.dobMonth
                    dobYear = viewModel.dobYear
                    years = viewModel.years
                    months = viewModel.months
                    days = viewModel.days
                    phoneNumber = viewModel.phoneNumber
                    gender = viewModel.gender
                    isPersonDeceased = viewModel.isPersonDeceased
                    personDeceasedReason = viewModel.selectedDeceasedReason
                    motherName = viewModel.motherName.capitalizeFirst()
                    fatherName = viewModel.fatherName.capitalizeFirst()
                    spouseName = viewModel.spouseName.capitalizeFirst()
                }
                patientRegistrationViewModel.currentStep = 2
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .testTag("step2"),
            enabled = viewModel.basicInfoValidation()
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
    if (viewModel.showDeceasedReasonSheet) {
        DeceasedReasonComposable(
            selectedReason = viewModel.selectedDeceasedReason,
            dismiss = {
                viewModel.showDeceasedReasonSheet = false
            },
            updatedReasons = { reasons ->
                viewModel.selectedDeceasedReason = reasons
                viewModel.showDeceasedReasonSheet = false
                viewModel.isPersonDeceased = YesNoEnum.YES.code
            }
        )
    }
}

@Composable
fun DobTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomTextField(
                value = viewModel.dobDay,
                label = stringResource(id = R.string.day),
                weight = 0.23f,
                maxLength = 2,
                isError = false,
                error = "",
                KeyboardType.Number,
                KeyboardCapitalization.None
            ) {
                if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.dobDay = it
                if (viewModel.dobDay.isNotEmpty()) {
                    viewModel.monthsList = getMonthsList(viewModel.dobDay)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            MonthDropDown(viewModel)
            Spacer(modifier = Modifier.width(10.dp))
            CustomTextField(
                value = viewModel.dobYear,
                label = stringResource(id = R.string.year),
                weight = 1f,
                maxLength = 4,
                isError = false,
                error = "",
                KeyboardType.Number,
                KeyboardCapitalization.None
            ) {
                if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.dobYear = it
            }
        }
        DateErrorText(viewModel)
    }
}

@Composable
private fun DateErrorText(viewModel: PatientRegistrationStepOneViewModel) {
    if (viewModel.dobDay.isNotEmpty() && viewModel.dobMonth.isNotEmpty() && viewModel.dobYear.isNotEmpty()
        && !isDOBValid(
            viewModel.dobDay.toInt(),
            viewModel.dobMonth.toMonthInteger(),
            viewModel.dobYear.toInt()
        )
    ) {
        Text(
            text = stringResource(
                id = R.string.invalid_date,
                "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}"
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun MonthDropDown(viewModel: PatientRegistrationStepOneViewModel) {
    var monthExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .testTag("Month")
    ) {
        OutlinedTextField(
            value = viewModel.dobMonth,
            onValueChange = {
                viewModel.dobMonth = it
            },
            label = {
                Text(text = stringResource(id = R.string.month))
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "")
            },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            monthExpanded = !monthExpanded
                        }
                    }
                }
            },
            readOnly = true,
            singleLine = true
        )
        DropdownMenu(
            modifier = Modifier.fillMaxHeight(0.5f),
            expanded = monthExpanded,
            onDismissRequest = { monthExpanded = false },
        ) {
            viewModel.monthsList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        monthExpanded = false
                        viewModel.dobMonth = label
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
fun AgeTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AgeYearsComposable(viewModel)
        Spacer(modifier = Modifier.width(15.dp))
        AgeMonthsComposable(viewModel)
        Spacer(modifier = Modifier.width(15.dp))
        AgeDaysComposable(viewModel)
    }
}

@Composable
private fun AgeDaysComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.days,
        label = stringResource(id = R.string.age_days),
        0.5F,
        2,
        viewModel.isAgeDaysValid,
        stringResource(
            id = R.string.age_days_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.days = it
        if (viewModel.days.isNotEmpty()) viewModel.isAgeDaysValid =
            viewModel.days.toInt() < 1 || viewModel.days.toInt() > 30
    }
}

@Composable
private fun AgeMonthsComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.months,
        label = stringResource(id = R.string.age_months),
        0.36F,
        2,
        viewModel.isAgeMonthsValid,
        stringResource(
            id = R.string.age_months_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.months = it
        if (viewModel.months.isNotEmpty()) viewModel.isAgeMonthsValid =
            viewModel.months.toInt() < 1 || viewModel.months.toInt() > 11
    }
}

@Composable
private fun AgeYearsComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.years,
        label = stringResource(id = R.string.age_years),
        0.25F,
        3,
        viewModel.isAgeYearsValid,
        stringResource(
            id = R.string.age_years_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.years = it
        if (viewModel.years.isNotEmpty()) viewModel.isAgeYearsValid =
            viewModel.years.toInt() < 0 || viewModel.years.toInt() > 150
    }
}

@Composable
fun GenderComposable(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("genderRow"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.gender_mandatory),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(
            viewModel.gender,
            GenderEnum.MALE.value,
            stringResource(id = R.string.male)
        ) {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            viewModel.gender,
            GenderEnum.FEMALE.value,
            stringResource(id = R.string.female)
        ) {
            viewModel.gender = it
        }
    }
}

@Composable
fun PatientDeceasedComposable(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.patient_deceased),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(
            viewModel.isPersonDeceased.toString(),
            YesNoEnum.YES.code.toString(),
            YesNoEnum.YES.display
        ) {
            viewModel.showDeceasedReasonSheet = true
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            viewModel.isPersonDeceased.toString(),
            YesNoEnum.NO.code.toString(),
            YesNoEnum.NO.display
        ) {
            viewModel.isPersonDeceased = it.toInt()
            viewModel.selectedDeceasedReason = ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeceasedReasonComposable(
    selectedReason: String,
    dismiss: () -> Unit,
    updatedReasons: (String) -> Unit
) {
    val selectedDeceasedReason = remember { mutableStateListOf<String>() }

    var otherReason by remember { mutableStateOf("") }
    var isOtherError by remember { mutableStateOf(false) }

    selectedReason.split(",").forEach { reason ->
        if (getDeceasedReasonList().contains(reason)) selectedDeceasedReason.add(reason)
        else if (reason.isNotBlank()) {
            selectedDeceasedReason.add(DeceasedReason.OTHERS.reason)
            otherReason = reason
        }
    }
    ModalBottomSheet(
        onDismissRequest = {
            dismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier
            .navigationBarsPadding(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.person_deceased_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.select_decease_reason),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            getDeceasedReasonList().forEach { reason ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedDeceasedReason.contains(reason),
                        onCheckedChange = { checked ->
                            if (checked) selectedDeceasedReason.add(reason)
                            else {
                                selectedDeceasedReason.remove(reason)
                                if (reason == DeceasedReason.OTHERS.reason) {
                                    otherReason = ""
                                    isOtherError = false
                                }
                            }
                        }
                    )
                    Text(
                        text = reason
                    )
                }
            }
            AnimatedVisibility(
                visible = selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)
            ) {
                CustomTextFieldWithLength(
                    value = otherReason,
                    label = null,
                    placeholder = stringResource(R.string.please_specify),
                    weight = 1f,
                    maxLength = 50,
                    isError = isOtherError,
                    error = stringResource(R.string.specify_reason),
                    keyboardType = KeyboardType.Text,
                    keyboardCapitalization = KeyboardCapitalization.Sentences
                ) {
                    otherReason = it.trim()
                    isOtherError = selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)
                            && otherReason.isBlank()
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = dismiss
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(Modifier.width(16.dp))
                TextButton(
                    onClick = {
                        if (selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)) {
                            selectedDeceasedReason.remove(DeceasedReason.OTHERS.reason)
                            selectedDeceasedReason.add(otherReason)
                        }
                        updatedReasons(selectedDeceasedReason.joinToString(","))
                    },
                    enabled = selectedDeceasedReason.isNotEmpty() && !(selectedDeceasedReason.contains(
                        DeceasedReason.OTHERS.reason
                    )
                            && otherReason.isBlank())
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}