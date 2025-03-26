package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.ui.common.CustomFilterChip
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.theme.Neutral40
import com.latticeonfhir.android.utils.converters.responseconverter.MonthsList.getMonthsList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isDOBValid
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.latticeonfhir.android.utils.regex.NameRegex.nameRegex
import com.latticeonfhir.android.utils.regex.PhoneNumberRegex.phoneNumberRegex
import java.util.Locale

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
                viewModel.middleName = middleName.toString()
                viewModel.lastName = lastName.toString()
                viewModel.phoneNumber = phoneNumber.toString()
                viewModel.email = email.toString()
                viewModel.dobAgeSelector = dobAgeSelector.toString()
                viewModel.dobDay = dobDay.toString()
                viewModel.dobMonth = dobMonth.toString()
                viewModel.dobYear = dobYear.toString()
                viewModel.years = years.toString()
                viewModel.months = months.toString()
                viewModel.days = days.toString()
                viewModel.gender = gender.toString()
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
                .weight(1f)
        ) {

            CustomTextField(
                viewModel.firstName,
                stringResource(id = R.string.first_name),
                1f,
                viewModel.maxFirstNameLength,
                viewModel.isNameValid,
                stringResource(id = R.string.first_name_error_msg),
                KeyboardType.Text,
                KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.firstName = it.trim()
                viewModel.isNameValid =
                    viewModel.firstName.length < 3 || viewModel.firstName.length > 100
            }
            ValueLength(viewModel.firstName, "FIRST_NAME_LENGTH")
            CustomTextField(
                viewModel.middleName,
                stringResource(id = R.string.middle_name),
                1f,
                viewModel.maxMiddleNameLength,
                false,
                "",
                KeyboardType.Text,
                KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.middleName = it.trim()
            }
            ValueLength(viewModel.middleName, "MIDDLE_NAME_LENGTH")
            CustomTextField(
                viewModel.lastName,
                stringResource(id = R.string.last_name),
                1f,
                viewModel.maxLastNameLength,
                false,
                "",
                KeyboardType.Text,
                KeyboardCapitalization.Words
            ) {
                if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.lastName = it.trim()
            }
            ValueLength(viewModel.lastName, "LAST_NAME_LENGTH")
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

            Spacer(modifier = Modifier.height(20.dp))

            ContactTextField(viewModel)

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                viewModel.email,
                stringResource(id = R.string.email),
                1F,
                viewModel.maxEmailLength,
                viewModel.isEmailValid,
                stringResource(id = R.string.email_error_msg),
                KeyboardType.Email,
                KeyboardCapitalization.None
            ) {
                viewModel.email = it
                viewModel.isEmailValid = !Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()
            }

            Spacer(modifier = Modifier.height(10.dp))

            GenderComposable(viewModel)
        }
        Button(
            onClick = {
                patientRegister.run {
                    firstName = viewModel.firstName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    middleName = viewModel.middleName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    lastName = viewModel.lastName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    dobAgeSelector = viewModel.dobAgeSelector
                    dobDay = viewModel.dobDay
                    dobMonth = viewModel.dobMonth
                    dobYear = viewModel.dobYear
                    years = viewModel.years
                    months = viewModel.months
                    days = viewModel.days
                    phoneNumber = viewModel.phoneNumber
                    email = viewModel.email
                    gender = viewModel.gender
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
}

@Composable
fun ValueLength(value: String, tag: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, end = 15.dp)
            .testTag(tag),
        text = if (value.isEmpty()) "" else "${value.length}/100",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Right,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
fun ContactTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "IND (+91)",
            onValueChange = {},
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .testTag("COUNTRY_CODE"),
            readOnly = true,
            singleLine = true
        )
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = {
                if (it.length <= 10 && (it.matches(viewModel.onlyNumbers) || it.isEmpty()))
                    viewModel.phoneNumber = it
                viewModel.isPhoneValid = !viewModel.phoneNumber.matches(phoneNumberRegex)
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .testTag("Phone Number"),
            placeholder = {
                Text(text = stringResource(id = R.string.enter_phone_number))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            isError = viewModel.isPhoneValid,
            supportingText = {
                if (viewModel.isPhoneValid)
                    Text(
                        stringResource(id = R.string.phone_number_error_msg),
                        style = MaterialTheme.typography.bodySmall
                    )
            }
        )
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
            text = stringResource(id = R.string.gender),
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
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            viewModel.gender,
            GenderEnum.OTHER.value,
            stringResource(id = R.string.other)
        ) {
            viewModel.gender = it
        }
    }
}

