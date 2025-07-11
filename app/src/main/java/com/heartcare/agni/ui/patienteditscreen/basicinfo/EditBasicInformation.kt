package com.heartcare.agni.ui.patienteditscreen.basicinfo

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomFilterChip
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.step1.DeceasedReasonComposable
import com.heartcare.agni.utils.converters.responseconverter.MonthsList.getMonthsList
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isDOBValid
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.heartcare.agni.utils.regex.NameRegex.nameRegex
import com.heartcare.agni.utils.regex.PhoneNumberRegex.phoneNumberRegex
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
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.basic_information),
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
                    TextButton(
                        onClick = {
                            if (viewModel.revertChanges()) {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("Changes undone")
                                }
                            }
                        },
                        enabled = viewModel.checkIsEdit()
                    ) {
                        Text(text = stringResource(R.string.undo_all))
                    }
                }
            )
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.lastName =
                            it.trim()
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
                        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.firstName =
                            it.trim()
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
                        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.motherName =
                            it.trim()
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
                        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.fatherName =
                            it.trim()
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
                        if (it.trim().matches(nameRegex) || it.isEmpty()) viewModel.spouseName =
                            it.trim()
                    }
                    Spacer(Modifier.height(64.dp))
                }
            }
        },
        floatingActionButton = {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        handleBasicInfoNavigation(viewModel, navController, patientResponse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 30.dp)
                        .testTag("step2"),
                    enabled = viewModel.basicInfoValidation() && viewModel.checkIsEdit()
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    )

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
fun PatientDeceasedComposable(viewModel: EditBasicInformationViewModel) {
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
        }
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
            lastName = viewModel.lastName,
            mobileNumber = viewModel.phoneNumber.ifBlank { null }?.toLong(),
            birthDate = if (viewModel.dobAgeSelector == "dob") "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}".toPatientDate()
            else ageToPatientDate(
                viewModel.years.toIntOrNull() ?: 0,
                viewModel.months.toIntOrNull() ?: 0,
                viewModel.days.toIntOrNull() ?: 0
            ).toPatientDate(),
            gender = viewModel.gender,
            mothersName = viewModel.motherName,
            fathersName = viewModel.fatherName.ifBlank { null },
            spouseName = viewModel.spouseName.ifBlank { null },
            patientDeceasedReason = viewModel.selectedDeceasedReason.ifBlank { null }
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
                viewModel.lastName = lastName
                viewModel.phoneNumber = mobileNumber?.toString() ?: ""
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

                viewModel.motherName = mothersName
                viewModel.fatherName = fathersName ?: ""
                viewModel.spouseName = spouseName ?: ""
                viewModel.isPersonDeceased = if (patientDeceasedReason.isNullOrBlank()) 0 else 1
                viewModel.selectedDeceasedReason = patientDeceasedReason ?: ""
            }
            viewModel.isLaunched = true

            //set temp value
            viewModel.firstNameTemp = viewModel.firstName
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
            viewModel.motherNameTemp = viewModel.motherName
            viewModel.fatherNameTemp = viewModel.fatherName
            viewModel.spouseNameTemp = viewModel.spouseName
            viewModel.isPersonDeceasedTemp = viewModel.isPersonDeceased
            viewModel.selectedDeceasedReasonTemp = viewModel.selectedDeceasedReason
        }
    }

}

@Composable
fun DobTextField(viewModel: EditBasicInformationViewModel) {
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
            modifier = Modifier.padding(start = 8.dp)
        )
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
fun AgeTextField(viewModel: EditBasicInformationViewModel) {
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.days = it
        if (viewModel.days.isNotEmpty()) viewModel.isAgeDaysValid =
            viewModel.days.toInt() < 1 || viewModel.days.toInt() > 30
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.months = it
        if (viewModel.months.isNotEmpty()) viewModel.isAgeMonthsValid =
            viewModel.months.toInt() < 1 || viewModel.months.toInt() > 11
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
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) viewModel.years = it
        if (viewModel.years.isNotEmpty()) viewModel.isAgeYearsValid =
            viewModel.years.toInt() < 0 || viewModel.years.toInt() > 150
    }
}

@Composable
fun GenderComposable(viewModel: EditBasicInformationViewModel) {
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
