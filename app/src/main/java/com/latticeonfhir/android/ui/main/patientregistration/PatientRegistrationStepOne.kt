@file:OptIn(ExperimentalMaterial3Api::class)

package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.ui.theme.Neutral10
import com.latticeonfhir.android.ui.main.ui.theme.Neutral40
import com.latticeonfhir.android.ui.main.ui.theme.Primary40
import com.marosseleng.compose.material3.datetimepickers.date.domain.DatePickerStroke
import java.time.LocalDate

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PatientRegistrationStepOne(viewModel: PatientRegistrationViewModel) {
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
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral10
            )
            Text(
                text = "Page 1/3",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(11f)
        ) {

            CustomTextField(viewModel.firstName, "First Name", 1f, viewModel.maxFirstNameLength) {
                viewModel.firstName = it
            }
            ValueLength(viewModel.firstName)
            CustomTextField(
                viewModel.middleName,
                "Middle Name",
                1f,
                viewModel.maxMiddleNameLength
            ) {
                viewModel.middleName = it
            }
            ValueLength(viewModel.middleName)
            CustomTextField(viewModel.lastName, "Last Name", 1f, viewModel.maxLastNameLength) {
                viewModel.lastName = it
            }
            ValueLength(viewModel.lastName)
            Row(modifier = Modifier.fillMaxWidth()) {
                AssistChip(
                    onClick = { viewModel.dobAgeSelector = "dob" },
                    label = { Text(text = "Date of birth") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (viewModel.dobAgeSelector == "dob")
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.background,
                        labelColor = if (viewModel.dobAgeSelector == "dob")
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                AssistChip(
                    onClick = { viewModel.dobAgeSelector = "age" },
                    label = { Text(text = "Age") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (viewModel.dobAgeSelector == "age")
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.background,
                        labelColor = if (viewModel.dobAgeSelector == "age")
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                )
            }
            if (viewModel.dobAgeSelector == "dob") {
                Spacer(modifier = Modifier.height(8.dp))
                DobTextField(viewModel)
            } else
                AgeTextField(viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            ContactTextField(viewModel)

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(viewModel.email, "Email", 1F, viewModel.maxEmailLength) {
                viewModel.email = it
            }

            Spacer(modifier = Modifier.height(10.dp))

            GenderComposable(viewModel)
        }
        Button(
            onClick = {
                viewModel.step = 2
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .weight(0.9f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary40
            ),
            enabled = viewModel.basicInfoValidation()
        ) {
            Text(text = "Next")
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    label: String,
    weight: Float,
    maxLength: Int,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength)
                updateValue(it)
        },
        modifier = Modifier.fillMaxWidth(weight),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        maxLines = 1,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary40
        )
    )
}

@Composable
fun ValueLength(value: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, end = 15.dp),
        text = if (value.length == 0) "" else "${value.length}/150",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Right,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DobTextField(viewModel: PatientRegistrationViewModel) {
    var isDateDialogShown: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    if (isDateDialogShown) {
        com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog(
            onDismissRequest = { isDateDialogShown = false },
            onDateChange = {
                viewModel.dob = it.toString()
                isDateDialogShown = false
            },
            title = { Text(text = "Select date") },
            initialDate = LocalDate.now(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            colors = com.marosseleng.compose.material3.datetimepickers.date.domain.DatePickerDefaults.colors(
                todayStroke = DatePickerStroke(1.dp, Primary40),
                todayLabelTextColor = Primary40,
                monthDayLabelSelectedBackgroundColor = Primary40,
                selectedYearBackgroundColor = Primary40,
                selectedMonthBackgroundColor = Primary40
            )
        )
    }
    OutlinedTextField(
        value = viewModel.dob,
        onValueChange = {},
        placeholder = { Text(text = "Date of birth", style = MaterialTheme.typography.bodyLarge)},
        interactionSource = remember {
            MutableInteractionSource()
        }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release) {
                        isDateDialogShown = true
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary40
        )
    )
}

@Composable
fun AgeTextField(viewModel: PatientRegistrationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(viewModel.years, label = "Years", 0.25F, 3) {
            viewModel.years = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(viewModel.months, label = "Months", 0.36F, 2) {
            viewModel.months = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(viewModel.days, label = "Days", 0.5F, 2) {
            viewModel.days = it
        }
    }
}

@Composable
fun ContactTextField(viewModel: PatientRegistrationViewModel) {
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary40
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = {
                if (it.length <= 10)
                    viewModel.phoneNumber = it
            },
            modifier = Modifier.fillMaxWidth(1f),
            placeholder = {
                Text(text = "Enter Phone Number")
            },
            maxLines = 1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary40
            )
        )
    }
}

@Composable
fun GenderComposable(viewModel: PatientRegistrationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Gender", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(20.dp))
        AssistChip(
            onClick = { viewModel.gender = "male" },
            label = {
                Text(text = "Male")
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (viewModel.gender == "male")
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.background,
                labelColor = if (viewModel.gender == "male")
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(15.dp))
        AssistChip(
            onClick = { viewModel.gender = "female" },
            label = {
                Text(text = "Female")
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (viewModel.gender == "female")
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.background,
                labelColor = if (viewModel.gender == "female")
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(15.dp))
        AssistChip(
            onClick = { viewModel.gender = "others" },
            label = {
                Text(text = "Others")
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (viewModel.gender == "others")
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.background,
                labelColor = if (viewModel.gender == "others")
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.outline
            )
        )
    }
}