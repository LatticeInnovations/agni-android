package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.ui.common.CustomFilterChip
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister

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
                text = "Basic Information",
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
                "First Name",
                1f,
                viewModel.maxFirstNameLength,
                viewModel.isNameValid,
                "Name length should be between 3 and 100.",
                KeyboardType.Text
            ) {
                viewModel.firstName = it
                viewModel.isNameValid =
                    viewModel.firstName.length < 3 || viewModel.firstName.length > 100
            }
            ValueLength(viewModel.firstName)
            CustomTextField(
                viewModel.middleName,
                "Middle Name",
                1f,
                viewModel.maxMiddleNameLength,
                false,
                "",
                KeyboardType.Text
            ) {
                viewModel.middleName = it
            }
            ValueLength(viewModel.middleName)
            CustomTextField(
                viewModel.lastName,
                "Last Name",
                1f,
                viewModel.maxLastNameLength,
                false,
                "",
                KeyboardType.Text
            ) {
                viewModel.lastName = it
            }
            ValueLength(viewModel.lastName)
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
                "Email",
                1F,
                viewModel.maxEmailLength,
                viewModel.isEmailValid,
                "Enter valid userEmail (eg., abc123@gmail.com)",
                KeyboardType.Email
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
                    firstName = viewModel.firstName.capitalize()
                    middleName = viewModel.middleName.capitalize()
                    lastName = viewModel.lastName.capitalize()
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
            Text(text = "Next")
        }
    }
}

@Composable
fun ValueLength(value: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, end = 15.dp),
        text = if (value.isEmpty()) "" else "${value.length}/100",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Right,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun DobTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var monthExpanded by remember { mutableStateOf(false) }
        var errorMsg by remember {
            mutableStateOf("")
        }
        CustomTextField(
            value = viewModel.dobDay,
            label = "Day",
            weight = 0.23f,
            maxLength = 2,
            isError = viewModel.isDobDayValid,
            error = errorMsg,
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers)|| it.length == 0) viewModel.dobDay = it
            if (viewModel.dobDay.isNotEmpty()) {
                viewModel.isDobDayValid =
                    viewModel.dobDay.toInt() < 1 || viewModel.dobDay.toInt() > 31
                errorMsg = "Enter valid day between 1 and 31."
                if (viewModel.dobMonth == "February") {
                    viewModel.isDobDayValid =
                        viewModel.dobDay.toInt() < 1 || viewModel.dobDay.toInt() > 29
                    errorMsg = "Enter valid day between 1 and 29."
                }
                viewModel.getMonthsList()
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
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
                    Text(text = "Month")
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
        Spacer(modifier = Modifier.width(10.dp))
        CustomTextField(
            value = viewModel.dobYear,
            label = "Year",
            weight = 1f,
            maxLength = 4,
            isError = viewModel.isDobYearValid,
            error = "Enter valid year between 1900 and 2023",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers) || it.length == 0) viewModel.dobYear = it
            if (viewModel.dobYear.isNotEmpty()) {
                viewModel.isDobYearValid =
                    viewModel.dobYear.toInt() < 1900 || viewModel.dobYear.toInt() > 2023
            }
        }
    }
}

@Composable
fun AgeTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(
            viewModel.years, label = "Years", 0.25F, 3, viewModel.isAgeYearsValid, "Enter valid input between 0 to 150.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers)|| it.length == 0) viewModel.years = it
            if (viewModel.years.isNotEmpty()) viewModel.isAgeYearsValid = viewModel.years.toInt() < 0 || viewModel.years.toInt() > 150
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            viewModel.months, label = "Months", 0.36F, 2, viewModel.isAgeMonthsValid, "Enter valid input between 1 and 11.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers)|| it.length == 0) viewModel.months = it
            if (viewModel.months.isNotEmpty()) viewModel.isAgeMonthsValid = viewModel.months.toInt() < 1 || viewModel.months.toInt() > 11
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            viewModel.days, label = "Days", 0.5F, 2, viewModel.isAgeDaysValid, "Enter valid input between 1 and 30.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers)|| it.length == 0) viewModel.days = it
            if (viewModel.days.isNotEmpty()) viewModel.isAgeDaysValid = viewModel.days.toInt() < 1 || viewModel.days.toInt() > 30
        }
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
            modifier = Modifier.fillMaxWidth(0.4f),
            readOnly = true,
            singleLine = true
        )
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = {
                viewModel.isPhoneValid = it.length < 10
                if (it.length <= 10 && (it.matches(viewModel.onlyNumbers)|| it.length == 0))
                    viewModel.phoneNumber = it
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .testTag("Phone Number"),
            placeholder = {
                Text(text = "Enter Phone Number")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            isError = viewModel.isPhoneValid,
            supportingText = {
                if (viewModel.isPhoneValid)
                    Text("Enter Valid Input", style = MaterialTheme.typography.bodySmall)
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
        Text(text = "Gender", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(viewModel.gender, GenderEnum.MALE.value, "Male") {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(viewModel.gender, GenderEnum.FEMALE.value, "Female") {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(viewModel.gender, GenderEnum.OTHER.value, "Other") {
            viewModel.gender = it
        }
    }
}

