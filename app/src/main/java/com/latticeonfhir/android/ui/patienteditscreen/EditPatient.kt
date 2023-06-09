package com.latticeonfhir.android.ui.patienteditscreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.ui.patientregistration.preview.PatientRegistrationPreviewViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationEnumFromString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatient(
    navController: NavController,
    viewModel: EditPatientViewModel = hiltViewModel()
) {
    viewModel.patient =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
            key = "patient_details"
        )


    LaunchedEffect(Unit) {
        Timber.tag("PatientIds").d(viewModel.patient?.id)
        viewModel.patient = viewModel.getPatientData(viewModel.patient!!.id)
        Timber.tag("PatientIdsss").d(viewModel.patient?.middleName)

        viewModel.patient?.run {
            viewModel.firstName = firstName
            viewModel.middleName = middleName ?: ""
            viewModel.lastName = lastName ?: ""
            viewModel.email = email ?: ""
            viewModel.phoneNumber = mobileNumber.toString()
            viewModel.dob = birthDate
            viewModel.gender = gender
            identifier.forEach { identity ->
                Timber.tag("identifier")
                    .d(identity.identifierType + "  " + identity.identifierNumber + " " + identity.code)
                when (identity.identifierType) {
                    "https://www.passportindia.gov.in/" -> {
                        viewModel.passportId = identity.identifierNumber
                    }

                    "https://www.nvsp.in/" -> {
                        viewModel.voterId = identity.identifierNumber

                    }

                    "https://www.apollohospitals.com/" -> {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                            contentDescription = "Back button"
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
                if (viewModel.patient != null) {
                    PreviewScreen(navController, viewModel, viewModel.patient!!)
                } else {
                    Timber.d("PatientResponse is empty")
                }
            }
        }
    )
}

@Composable
fun PreviewScreen(
    navController: NavController,
    viewModel: EditPatientViewModel,
    patientResponse: PatientResponse
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
                Heading("Basic Information", 1, patientResponse, navController)
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
                if (!viewModel.email.isEmpty()) {
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
                Heading("Identification", 2, patientResponse, navController)
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
                Heading("Addresses", 3, patientResponse, navController)
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
fun DiscardDialog(navController: NavController, fromHousehold: Boolean, closeDialog: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            closeDialog()
        },
        title = {
            Text(
                text = "Discard Changes?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("alert dialog title")
            )
        },
        text = {
            Text(
                "Are you sure you want to discard this patient record?",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("alert dialog description")
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog()
                    if (fromHousehold)
                        navController.popBackStack(Screen.AddHouseholdMember.route, false)
                    else navController.popBackStack(Screen.LandingScreen.route, false)
                }) {
                Text(
                    "Yes, discard",
                    modifier = Modifier.testTag("alert dialog confirm btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }) {
                Text(
                    "No, go back",
                    modifier = Modifier.testTag("alert dialog cancel btn")
                )
            }
        }
    )
}

@Composable
fun Heading(
    heading: String,
    step: Int,
    patientResponse: PatientResponse,
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
                    "currentStep",
                    step
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient_register_details",
                    patientResponse
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
                text = context.getString(R.string.edit_btn),
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