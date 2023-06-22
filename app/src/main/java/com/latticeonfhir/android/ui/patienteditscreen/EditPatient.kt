package com.latticeonfhir.android.ui.patienteditscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.constants.IdentificationConstants
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatient(
    navController: NavController,
    viewModel: EditPatientViewModel = hiltViewModel()
) {
    viewModel.id =
        navController.previousBackStackEntry?.savedStateHandle?.get<String>(
            key = "patient_detailsID"
        ).toString()
    viewModel.isProfileUpdated =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("isProfileUpdated") == true


    LaunchedEffect(viewModel.isProfileUpdated) {
        withContext(Dispatchers.IO) {
            if (viewModel.id == "null") viewModel.id = viewModel.patientResponse!!.id
            viewModel.patientResponse = viewModel.getPatientData(viewModel.id)
            viewModel.patientResponse?.run {
                viewModel.firstName = firstName
                viewModel.middleName = middleName ?: ""
                viewModel.lastName = lastName ?: ""
                viewModel.email = email ?: ""
                viewModel.phoneNumber = mobileNumber.toString()
                viewModel.dob = birthDate.toPatientPreviewDate()
                viewModel.gender = gender
                viewModel.identifier = identifier.toMutableList()
                viewModel.passportId = ""
                viewModel.patientId = ""
                viewModel.voterId = ""
                viewModel.identifier.forEach { identity ->
                    when (identity.identifierType) {
                        IdentificationConstants.PASSPORT_TYPE -> {
                            viewModel.passportId = identity.identifierNumber
                        }

                        IdentificationConstants.VOTER_ID_TYPE -> {
                            viewModel.voterId = identity.identifierNumber

                        }

                        IdentificationConstants.PATIENT_ID_TYPE -> {
                            viewModel.patientId = identity.identifierNumber
                        }

                        else -> {}

                    }
                }

                viewModel.homeAddress.pincode = permanentAddress.postalCode
                viewModel.homeAddress.state = permanentAddress.state
                viewModel.homeAddress.addressLine1 = permanentAddress.addressLine1
                viewModel.homeAddress.addressLine2 = permanentAddress.addressLine2 ?: ""
                viewModel.homeAddress.city = permanentAddress.city
                viewModel.homeAddress.district = permanentAddress.district ?: ""

            }
        }

    }

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Patient profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "BACK_ICON"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )

            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (viewModel.patientResponse != null) {

                    PreviewScreen(navController, viewModel)
                } else {
                    Timber.d("PatientResponse is empty")
                }

                if (viewModel.isProfileUpdated) {
                    LaunchedEffect(true) {
                        snackBarHostState.showSnackbar("Profile updated successfully")
                        viewModel.isProfileUpdated = false
                    }
                }
            }
        }
    )
}

@Composable
fun PreviewScreen(
    navController: NavController,
    viewModel: EditPatientViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
            .testTag("columnLayout")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {


                Heading("Basic Information", 1, viewModel, navController)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${
                        NameConverter.getFullName(
                            viewModel.firstName,
                            viewModel.middleName,
                            viewModel.lastName
                        )
                    }, ${viewModel.gender.capitalize()}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("NAME_TAG")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Label("Date of birth")
                Detail(viewModel.dob, "DOB_TAG")
                Spacer(modifier = Modifier.height(10.dp))
                Label("Phone No.")
                Detail("+91 ${viewModel.phoneNumber}", "PHONE_NO_TAG")
                if (viewModel.email.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Email")
                    Detail(viewModel.email, "EMAIL_TAG")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                Heading("Identification", 2, viewModel, navController)
                if (viewModel.passportId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Passport ID")
                    Detail(viewModel.passportId, "PASSPORT_ID_TAG")
                }
                if (viewModel.voterId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Voter ID")
                    Detail(viewModel.voterId, "VOTER_ID_TAG")
                }
                if (viewModel.patientId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Patient ID")
                    Detail(viewModel.patientId, "PATIENT_ID_TAG")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            val homeAddressLine1 = viewModel.homeAddress.addressLine1 +
                    if (viewModel.homeAddress.addressLine2.isEmpty()) "" else {
                        ", " + viewModel.homeAddress.addressLine2
                    }
            val homeAddressLine2 = viewModel.homeAddress.city +
                    if (viewModel.homeAddress.district.isEmpty()) "" else {
                        ", " + viewModel.homeAddress.district
                    }
            val homeAddressLine3 =
                "${viewModel.homeAddress.state}, ${viewModel.homeAddress.pincode}"
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Heading("Addresses", 3, viewModel, navController)
                Spacer(modifier = Modifier.height(10.dp))
                Label("Home Address")
                Detail(homeAddressLine1, "ADDRESS_LINE1_TAG")
                Detail(homeAddressLine2, "ADDRESS_LINE2_TAG")
                Detail(homeAddressLine3, "ADDRESS_LINE3_TAG")
//                if (viewModel.workAddress.pincode.isNotEmpty()) {
//                    val workAddressLine1 = viewModel.workAddress.addressLine1 +
//                            if (viewModel.workAddress.addressLine2.isEmpty()) "" else {
//                                ", " + viewModel.workAddress.addressLine2
//                            }
//                    val workAddressLine2 = viewModel.workAddress.city +
//                            if (viewModel.workAddress.district.isEmpty()) "" else {
//                                ", " + viewModel.workAddress.district
//                            }
//                    val workAddressLine3 =
//                        "${viewModel.workAddress.state}, ${viewModel.workAddress.pincode}"
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Label("Work Address")
//                    Detail(workAddressLine1)
//                    Detail(workAddressLine2)
//                    Detail(workAddressLine3)
//                }
            }
        }
//        if (viewModel.openDialog) {
//            DiscardDialog(navController, viewModel.fromHouseholdMember) {
//                viewModel.openDialog = false
//            }
//        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .testTag("end of page")
        )
    }

}

@Composable
fun Heading(
    heading: String,
    step: Int,
    viewModel: EditPatientViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        TextButton(
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "isEditing",
                    true
                )

                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient_details",
                    viewModel.patientResponse
                )
                when (step) {
                    1 -> navController.navigate(Screen.EditBasicInfo.route)
                    2 -> navController.navigate(Screen.EditIdentification.route)
                    3 -> navController.navigate(Screen.EditAddress.route)
                }

            },
            modifier = Modifier
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
                text = context.getString(R.string.edit),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Label(label: String) {
    Text(text = label, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun Detail(detail: String, tag: String) {
    Text(
        text = detail,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag(tag)
    )
}