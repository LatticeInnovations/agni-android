package com.latticeonfhir.android.ui.patienteditscreen.basicinfo

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomFilterChip
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.common.CustomTextFieldWithLength
import com.latticeonfhir.android.utils.converters.responseconverter.MonthsList.getMonthsList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isDOBValid
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.regex.NameRegex.nameRegex
import com.latticeonfhir.android.utils.regex.PhoneNumberRegex.phoneNumberRegex
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBasicInformation(
    navController: NavController,
    viewModel: EditBasicInformationViewModel = hiltViewModel()
) {
    val patientResponse =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>("patient_details")
    HandleLaunchedEffect(viewModel, patientResponse)
    BackHandler(enabled = true) {
        navController.previousBackStackEntry?.savedStateHandle?.set("isProfileUpdated", false)
        navController.popBackStack()
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Basic information",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            false
                        )
                        navController.popBackStack()

                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "clear icon"
                        )
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    Text(
                        text = "Undo all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.checkIsEdit()) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(viewModel.checkIsEdit(), onClick = {
                                if (viewModel.revertChanges()) {
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar("Changes undone")
                                    }
                                }
                            })

                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .testTag("columnLayout")
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FirstNameTextField(viewModel)
                    MiddleNameTextField(viewModel)
                    LastNameTextField(viewModel)
                    DOBAndAgeFields(viewModel)
                    Spacer(modifier = Modifier.height(1.dp))
                    GenderComposable(viewModel)
                    Spacer(modifier = Modifier.height(1.dp))
                    ContactTextField(viewModel)
                    EmailTextField(viewModel)
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        },
        floatingActionButton = {
            SaveButton(viewModel, patientResponse, navController)
        }
    )
    if (viewModel.showDOBWarning) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(stringResource(R.string.confirm_dob_update))
            },
            text = {
                Text(stringResource(R.string.confirm_dob_update_description))
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.showDOBWarning = false
                    }
                ) {
                    Text(stringResource(R.string.no_go_back))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // delete old immunization recommendation
                        viewModel.clearOldImmunizationRecommendation(patientResponse!!.id)
                        handleBasicInfoNavigation(viewModel, navController, patientResponse)
                        viewModel.showDOBWarning = false
                    }
                ) {
                    Text(stringResource(R.string.yes_continue))
                }
            }
        )
    }
}

@Composable
private fun FirstNameTextField(
    viewModel: EditBasicInformationViewModel
){
    CustomTextFieldWithLength(
        value = viewModel.firstName,
        label = stringResource(id = R.string.first_name),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxFirstNameLength,
        isError = viewModel.isNameValid,
        error = if (viewModel.firstName.isBlank())  stringResource(id = R.string.first_name_is_required)
        else stringResource(id = R.string.first_name_error_msg),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.firstName = it.trim()
        viewModel.isNameValid = viewModel.firstName.length !in 3..100
    }
}

@Composable
private fun MiddleNameTextField(
    viewModel: EditBasicInformationViewModel
){
    CustomTextFieldWithLength(
        value = viewModel.middleName,
        label = stringResource(id = R.string.middle_name),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxMiddleNameLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.middleName = it.trim()
    }
}

@Composable
private fun LastNameTextField(
    viewModel: EditBasicInformationViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.lastName,
        label = stringResource(id = R.string.last_name),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxLastNameLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.lastName = it.trim()
    }
}

@Composable
private fun DOBAndAgeFields(viewModel: EditBasicInformationViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
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

        if (viewModel.isDOBAgeBlank()) {
            Text(
                text = stringResource(id = R.string.dob_age_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun DobTextField(viewModel: EditBasicInformationViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DOBDayField(viewModel)
            MonthDropDown(viewModel)
            DOBYearField(viewModel)
        }
        DateErrorText(viewModel)
    }
}

@Composable
private fun DOBDayField(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.dobDay = it
        }
        if (viewModel.dobDay.isNotEmpty()) {
            viewModel.monthsList = getMonthsList(viewModel.dobDay)
        }
    }
}

@Composable
private fun MonthDropDown(viewModel: EditBasicInformationViewModel) {
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
private fun DOBYearField(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.dobYear = it
        }
    }
}

@Composable
private fun DateErrorText(viewModel: EditBasicInformationViewModel) {
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
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
private fun AgeTextField(viewModel: EditBasicInformationViewModel) {
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
private fun AgeDaysComposable(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.days = it
            viewModel.isAgeDaysValid = viewModel.days.isNotBlank() && viewModel.days.toInt() !in 1..30
        }
    }
}

@Composable
private fun AgeMonthsComposable(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.months = it
            viewModel.isAgeMonthsValid = viewModel.months.isNotBlank() && viewModel.months.toInt() !in 1..11
        }
    }
}

@Composable
private fun AgeYearsComposable(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.years = it
            viewModel.isAgeYearsValid = viewModel.years.isNotBlank() && viewModel.years.toInt() !in 0..150
        }
    }
}

@Composable
fun ContactTextField(viewModel: EditBasicInformationViewModel) {
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
                viewModel.isPhoneValid = !viewModel.phoneNumber.matches(phoneNumberRegex) && viewModel.phoneNumber.isNotBlank()
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
private fun EmailTextField(
    viewModel: EditBasicInformationViewModel
) {
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
}

@Composable
private fun GenderComposable(viewModel: EditBasicInformationViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
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
            Spacer(modifier = Modifier.width(15.dp))
            CustomFilterChip(
                viewModel.gender,
                GenderEnum.OTHER.value,
                stringResource(id = R.string.other)
            ) {
                viewModel.gender = it
            }
        }

        if (viewModel.gender.isEmpty()) {
            Text(
                text = stringResource( id = R.string.gender_is_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SaveButton(
    viewModel: EditBasicInformationViewModel,
    patientResponse: PatientResponse?,
    navController: NavController
) {
    Button(
        onClick = {
            val birthDate =
                if (viewModel.dobAgeSelector == "dob") "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}".toPatientDate()
                else ageToPatientDate(
                    viewModel.years.toIntOrNull() ?: 0,
                    viewModel.months.toIntOrNull() ?: 0,
                    viewModel.days.toIntOrNull() ?: 0
                ).toPatientDate()
            if (patientResponse!!.birthDate != birthDate) {
                viewModel.showDOBWarning = true
            } else
                handleBasicInfoNavigation(viewModel, navController, patientResponse)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, top = 6.dp)
            .testTag("step2"),
        enabled = viewModel.basicInfoValidation() && viewModel.checkIsEdit()
    ) {
        Text(text = "Save")
    }
}

private fun handleBasicInfoNavigation(
    viewModel: EditBasicInformationViewModel,
    navController: NavController,
    patientResponse: PatientResponse?
) {
    viewModel.updateBasicInfo(
        patientResponse!!.copy(
            firstName = viewModel.firstName,
            middleName = viewModel.middleName,
            lastName = viewModel.lastName,
            mobileNumber = viewModel.phoneNumber.ifBlank { null }?.toLong(),
            email = viewModel.email,
            birthDate = if (viewModel.dobAgeSelector == "dob") "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}".toPatientDate()
            else ageToPatientDate(
                viewModel.years.toIntOrNull() ?: 0,
                viewModel.months.toIntOrNull() ?: 0,
                viewModel.days.toIntOrNull() ?: 0
            ).toPatientDate(),
            gender = viewModel.gender
        )
    )
    navController.previousBackStackEntry?.savedStateHandle?.set(
        "isProfileUpdated",
        true
    )
    navController.popBackStack()
}

@Composable
fun HandleLaunchedEffect(
    viewModel: EditBasicInformationViewModel,
    patientResponse: PatientResponse?
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientResponse?.run {
                viewModel.firstName = firstName
                viewModel.middleName = middleName ?: ""
                viewModel.lastName = lastName ?: ""
                viewModel.phoneNumber = mobileNumber?.toString().orEmpty()
                viewModel.email = email ?: ""
                if (viewModel.dobRegex.matches(birthDate)) {
                    viewModel.dobAgeSelector = "dob"
                    val (day, month, year) = viewModel.splitDOB(birthDate)
                    viewModel.dobDay = day.toString()
                    viewModel.dobMonth = month
                    viewModel.dobYear = year.toString()
                } else if (viewModel.ageRegex.matches(birthDate)) {
                    val (day, month, year) = viewModel.splitAge(birthDate.toPatientDate())
                    viewModel.dobAgeSelector = "age"
                    viewModel.years = year.toString()
                    viewModel.months = month.toString()
                    viewModel.days = day.toString()
                }
                viewModel.gender = gender
                viewModel.birthDate = birthDate
            }
            viewModel.isLaunched = true

            //set temp value
            viewModel.firstNameTemp = viewModel.firstName
            viewModel.middleNameTemp = viewModel.middleName
            viewModel.lastNameTemp = viewModel.lastName
            viewModel.phoneNumberTemp = viewModel.phoneNumber
            viewModel.emailTemp = viewModel.email
            viewModel.dobAgeSelectorTemp = viewModel.dobAgeSelector
            viewModel.dobDayTemp = viewModel.dobDay
            viewModel.dobMonthTemp = viewModel.dobMonth
            viewModel.dobYearTemp = viewModel.dobYear
            viewModel.daysTemp = viewModel.days
            viewModel.monthsTemp = viewModel.months
            viewModel.yearsTemp = viewModel.years
            viewModel.genderTemp = viewModel.gender

        }
    }

}
