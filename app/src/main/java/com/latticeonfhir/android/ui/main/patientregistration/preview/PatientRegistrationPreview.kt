package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.patientregistration.preview.PatientRegistrationPreviewViewModel
import com.latticeonfhir.android.ui.main.ui.theme.Primary70
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.main.patientregistration.model.PatientRegister
import com.latticeonfhir.android.ui.main.patientregistration.step3.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationPreview(
    navController: NavController,
    viewModel: PatientRegistrationPreviewViewModel = viewModel()
) {
    val patientRegisterDetails = navController.previousBackStackEntry?.savedStateHandle?.get<PatientRegister>(
        key = "patient_register_details"
    )
    patientRegisterDetails
        ?.run {
            viewModel.firstName = firstName.toString()
            viewModel.middleName = middleName.toString()
            viewModel.lastName = lastName.toString()
            viewModel.email = email.toString()
            viewModel.phoneNumber = phoneNumber.toString()
            viewModel.dob = dob.toString()
            viewModel.years = years.toString()
            viewModel.months = months.toString()
            viewModel.days = days.toString()
            viewModel.gender = gender.toString()
            viewModel.passportId = passportId.toString()
            viewModel.voterId = voterId.toString()
            viewModel.patientId = patientId.toString()
            viewModel.homeAddress.pincode = homePostalCode.toString()
            viewModel.homeAddress.state = homeState.toString()
            viewModel.homeAddress.area = homeArea.toString()
            viewModel.homeAddress.city = homeCity.toString()
            viewModel.homeAddress.town = homeTown.toString()
            viewModel.workAddress.pincode = workPostalCode.toString()
            viewModel.workAddress.state = workState.toString()
            viewModel.workAddress.area = workArea.toString()
            viewModel.workAddress.city = workCity.toString()
            viewModel.workAddress.town = workTown.toString()
        }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Preview",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "isEditing",
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "step",
                            3
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient_register_details",
                            patientRegisterDetails
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = { viewModel.openDialog = true }) {
                        Icon(Icons.Default.Clear, contentDescription = "clear icon")
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (patientRegisterDetails != null) {
                    PreviewScreen(navController, viewModel, patientRegisterDetails)
                }
            }
        }
    )
}

@Composable
fun PreviewScreen(
    navController: NavController,
    viewModel: PatientRegistrationPreviewViewModel,
    patientRegister: PatientRegister
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .testTag("columnLayout")
                .weight(1f)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Basic Information")
                    DetailsText("First Name", viewModel.firstName)
                    if (viewModel.middleName != "") DetailsText("Middle Name", viewModel.middleName)
                    if (viewModel.lastName != "") DetailsText("Last Name", viewModel.lastName)
                    if (viewModel.dob.isNotEmpty()) DetailsText(
                        "Date of birth",
                        viewModel.dob
                    )
                    if (viewModel.years.isNotEmpty() && viewModel.months.isNotEmpty() && viewModel.days.isNotEmpty()) AgeText(
                        "Age",
                        viewModel.years,
                        viewModel.months,
                        viewModel.days
                    )
                    DetailsText("Phone Number", "+91 ${viewModel.phoneNumber}")
                    DetailsText("Email Address", viewModel.email)
                    DetailsText("Gender", viewModel.gender)
                    EditButton(navController, 1, patientRegister)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Identification")
                    if (viewModel.passportId.isNotEmpty()) DetailsText(
                        "Passport Id",
                        viewModel.passportId
                    )
                    if (viewModel.voterId.isNotEmpty()) DetailsText("Voter Id", viewModel.voterId)
                    if (viewModel.patientId.isNotEmpty()) DetailsText(
                        "Patient Id",
                        viewModel.patientId
                    )
                    EditButton(navController, 2, patientRegister)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Addresses")
                    SubHeading("Home Address")
                    AddressCard(viewModel.homeAddress)
                    if (viewModel.workAddress.pincode.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        SubHeading("Work Address")
                        AddressCard(viewModel.workAddress)
                    }
                    EditButton(navController, 3, patientRegister)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
            if (viewModel.openDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.openDialog = false
                    },
                    title = {
                        Text(
                            text = "Discard Changes ?",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.testTag("alert dialog title")
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to cancel preview and discard any changes you have made?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("alert dialog description")
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.openDialog = false
                                navController.navigate(Screen.LandingScreen.route)
                            }) {
                            Text(
                                "Confirm",
                                modifier = Modifier.testTag("alert dialog confirm btn")
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.openDialog = false
                            }) {
                            Text(
                                "Cancel",
                                modifier = Modifier.testTag("alert dialog cancel btn")
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.testTag("end of page"))
        }
        Button(
            onClick = {
                navController.navigate(Screen.LandingScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun Heading(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun SubHeading(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun DetailsText(label: String, text: String) {
    Text(
        text = "$label: ${text.capitalize()}",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 15.dp)
    )
}

@Composable
fun AgeText(label: String, years: String, months: String, days: String) {
    Text(
        text = "$label: $years years $months months $days days",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 15.dp)
    )
}

@Composable
fun EditButton(navController: NavController, step: Int, patientRegister: PatientRegister) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "isEditing",
                    true
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "step",
                    step
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient_register_details",
                    patientRegister
                )
                navController.navigate(Screen.PatientRegistrationScreen.route)
            },
            modifier = Modifier
                .align(Alignment.End)
                .testTag("edit btn $step")
        ) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddressCard(address: Address) {
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        DetailsText("Postal Code", address.pincode)
        DetailsText("State", address.state)
        DetailsText("House No., Building, Street, Area", address.area)
        DetailsText("Town/ Locality", address.town)
        DetailsText("City/ District", address.city)
    }
}