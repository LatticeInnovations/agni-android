package com.latticeonfhir.features.patient.ui.patienteditscreen.address

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.core.model.server.patient.PatientAddressResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.features.patient.ui.common.AddressComposable
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatientAddress(
    navController: NavController,
    viewModel: EditPatientAddressViewModel = hiltViewModel()
) {
    val patientResponse =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(key = "patient_details")
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientResponse?.run {
                viewModel.homeAddress.pincode = permanentAddress.postalCode
                viewModel.homeAddress.state = permanentAddress.state
                viewModel.homeAddress.city = permanentAddress.city
                viewModel.homeAddress.district = permanentAddress.district ?: ""
                viewModel.homeAddress.addressLine1 = permanentAddress.addressLine1
                viewModel.homeAddress.addressLine2 = permanentAddress.addressLine2 ?: ""

                viewModel.homeAddressTemp.pincode = viewModel.homeAddress.pincode
                viewModel.homeAddressTemp.state = viewModel.homeAddress.state
                viewModel.homeAddressTemp.city = viewModel.homeAddress.city
                viewModel.homeAddressTemp.district = viewModel.homeAddress.district
                viewModel.homeAddressTemp.addressLine1 = viewModel.homeAddress.addressLine1
                viewModel.homeAddressTemp.addressLine2 = viewModel.homeAddress.addressLine2
            }
        }
        viewModel.isLaunched = true

    }
    BackHandler(enabled = true) {
        navController.previousBackStackEntry?.savedStateHandle?.set("isProfileUpdated", false)
        navController.popBackStack()
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
                        text = "Address",
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
                colors = topAppBarColors(
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
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(it)
                        .testTag("columnLayout")
                ) {
                    AddressComposable(label = "Home Address", address = viewModel.homeAddress)
                    Spacer(modifier = Modifier.testTag("end of page"))
                }

                viewModel.isEditing = viewModel.checkIsEdit()
                Timber.tag("CheckEdit").d(viewModel.isEditing.toString())

            }

        }, floatingActionButton = {
            Button(
                onClick = {

                    viewModel.updateBasicInfo(
                        patientResponse!!.copy(
                            permanentAddress = PatientAddressResponse(
                                addressLine1 = viewModel.homeAddress.addressLine1,
                                city = viewModel.homeAddress.city,
                                district = viewModel.homeAddress.district.ifEmpty { null },
                                state = viewModel.homeAddress.state,
                                postalCode = viewModel.homeAddress.pincode,
                                country = "India",
                                addressLine2 = viewModel.homeAddress.addressLine2.ifEmpty { null },
                            )
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
                    .padding(top = 15.dp, start = 30.dp),
                enabled = viewModel.addressInfoValidation() && viewModel.isEditing
            ) {
                Text(text = "Save")
            }

        }
    )

}