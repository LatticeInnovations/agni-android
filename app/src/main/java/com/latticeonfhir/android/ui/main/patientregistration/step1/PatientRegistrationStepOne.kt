package com.latticeonfhir.android.ui.main.patientregistration.step1

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.ui.theme.Neutral40
import java.time.LocalDate
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.ui.main.common.CustomFilterChip
import com.latticeonfhir.android.ui.main.common.CustomTextField
import com.latticeonfhir.android.ui.main.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.main.patientregistration.model.PatientRegister
import java.time.format.DateTimeFormatter
import java.util.*

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
                text = "Page 1/3",
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
                "Name length should be between 3 and 150."
            ) {
                viewModel.firstName = it
                viewModel.isNameValid =
                    viewModel.firstName.length < 3 || viewModel.firstName.length > 150
            }
            ValueLength(viewModel.firstName)
            CustomTextField(
                viewModel.middleName,
                "Middle Name",
                1f,
                viewModel.maxMiddleNameLength,
                false,
                ""
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
                ""
            ) {
                viewModel.lastName = it
            }
            ValueLength(viewModel.lastName)
            Row(modifier = Modifier.fillMaxWidth()) {
                CustomFilterChip(viewModel.dobAgeSelector, "dob", "Date of Birth") {
                    viewModel.dobAgeSelector = it
                }
                Spacer(modifier = Modifier.width(10.dp))
                CustomFilterChip(viewModel.dobAgeSelector, "age", "Age") {
                    viewModel.dobAgeSelector = it
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
                "Enter valid email (eg., abc123@gmail.com)"
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
                    firstName = viewModel.firstName
                    middleName = viewModel.middleName
                    lastName = viewModel.lastName
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
                patientRegistrationViewModel.step = 2
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
        text = if (value.isEmpty()) "" else "${value.length}/150",
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
        CustomTextField(
            value = viewModel.dobDay,
            label = "Day",
            weight = 0.23f,
            maxLength = 2,
            isError = false,
            error = ""
        ){
            viewModel.dobDay = it
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .testTag("month")
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
            isError = false,
            error = ""
        ){
            viewModel.dobYear = it
        }
    }
}

@Composable
fun AgeTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(viewModel.years, label = "Years", 0.25F, 3, false, "") {
            viewModel.years = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(viewModel.months, label = "Months", 0.36F, 2, false, "") {
            viewModel.months = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(viewModel.days, label = "Days", 0.5F, 2, false, "") {
            viewModel.days = it
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
                if (it.length <= 10)
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
        CustomFilterChip(viewModel.gender, "male", "Male") {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(viewModel.gender, "female", "Female") {
            viewModel.gender = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(viewModel.gender, "others", "Others") {
            viewModel.gender = it
        }
    }
}

