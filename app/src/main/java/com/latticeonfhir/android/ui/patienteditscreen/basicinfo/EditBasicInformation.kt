package com.latticeonfhir.android.ui.patienteditscreen.basicinfo

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomFilterChip
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBasicInformation(
    navController: NavController,
    viewModel: EditBasicInformationViewModel = hiltViewModel()
) {
    val patientResponse =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>("patient_details")
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientResponse?.run {
                viewModel.firstName = firstName
                viewModel.middleName = middleName ?: ""
                viewModel.lastName = lastName ?: ""
                viewModel.phoneNumber = mobileNumber.toString()
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

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        color = if (viewModel.isEditing) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(viewModel.isEditing, onClick = {
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
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .testTag("columnLayout")
                        .weight(1f)
                        .padding(it)
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
                        if (viewModel.email.isNotEmpty()) {
                            viewModel.isEmailValid =
                                !Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()
                        } else {
                            viewModel.isEmailValid = false
                        }
                    }
                    ValueLengthEmail(viewModel.email)


                    Spacer(modifier = Modifier.height(10.dp))

                    GenderComposable(viewModel)
                    viewModel.isEditing = viewModel.checkIsEdit()
                }
                Button(
                    onClick = {

                        viewModel.updateBasicInfo(
                            patientResponse!!.copy(
                                firstName = viewModel.firstName,
                                middleName = viewModel.middleName,
                                lastName = viewModel.lastName,
                                mobileNumber = viewModel.phoneNumber.toLong(),
                                email = viewModel.email,
                                birthDate = if (viewModel.dobAgeSelector == "dob") "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}".toPatientDate()
                                else ageToPatientDate(
                                    viewModel.years.toInt(),
                                    viewModel.months.toInt(),
                                    viewModel.days.toInt()
                                ).toPatientDate(),
                                gender = viewModel.gender
                            )
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            true
                        )
                        navController.popBackStack()


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("step2"),
                    enabled = viewModel.basicInfoValidation() && viewModel.checkIsEdit()
                ) {
                    Text(text = "Save")
                }
            }
        }
    )

}

@Composable
fun ValueLength(value: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        text = if (value.isEmpty()) "" else "${value.length}/100",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Right,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun ValueLengthEmail(value: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        text = if (value.isEmpty()) "" else "${value.length}/150",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Right,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun DobTextField(viewModel: EditBasicInformationViewModel) {
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
            if (it.matches(viewModel.onlyNumbers) || it.length == 0) viewModel.dobDay = it
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
fun AgeTextField(viewModel: EditBasicInformationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(
            viewModel.years,
            label = "Years",
            0.25F,
            3,
            viewModel.isAgeYearsValid,
            "Enter valid input between 0 to 150.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers) || it.length == 0) viewModel.years = it
            if (viewModel.years.isNotEmpty()) viewModel.isAgeYearsValid =
                viewModel.years.toInt() < 0 || viewModel.years.toInt() > 150
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            viewModel.months,
            label = "Months",
            0.36F,
            2,
            viewModel.isAgeMonthsValid,
            "Enter valid input between 1 and 11.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers) || it.length == 0) viewModel.months = it
            if (viewModel.months.isNotEmpty()) viewModel.isAgeMonthsValid =
                viewModel.months.toInt() < 1 || viewModel.months.toInt() > 11
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            viewModel.days,
            label = "Days",
            0.5F,
            2,
            viewModel.isAgeDaysValid,
            "Enter valid input between 1 and 30.",
            KeyboardType.Number
        ) {
            if (it.matches(viewModel.onlyNumbers) || it.length == 0) viewModel.days = it
            if (viewModel.days.isNotEmpty()) viewModel.isAgeDaysValid =
                viewModel.days.toInt() < 1 || viewModel.days.toInt() > 30
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
            modifier = Modifier.fillMaxWidth(0.4f),
            readOnly = true,
            singleLine = true
        )
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = {
                viewModel.isPhoneValid = it.length < 10
                if (it.length <= 10 && (it.matches(viewModel.onlyNumbers) || it.length == 0))
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
fun GenderComposable(viewModel: EditBasicInformationViewModel) {
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

