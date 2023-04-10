package com.latticeonfhir.android.ui.patientregistration.step3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.AddressComposable
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.patientregistration.model.PatientRegister

@Composable
fun PatientRegistrationStepThree(navController: NavController, patientRegister: PatientRegister, viewModel: PatientRegistrationStepThreeViewModel = viewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.homeAddress.pincode = homePostalCode.toString()
                viewModel.homeAddress.state = homeState.toString()
                viewModel.homeAddress.city = homeCity.toString()
                viewModel.homeAddress.district = homeDistrict.toString()
                viewModel.homeAddress.addressLine1 = homeAddressLine1.toString()
                viewModel.homeAddress.addressLine2 = homeAddressLine2.toString()
                viewModel.addWorkAddress = workPostalCode.toString().isNotEmpty()
                viewModel.workAddress.pincode = workPostalCode.toString()
                viewModel.workAddress.state = workState.toString()
                viewModel.workAddress.city = workCity.toString()
                viewModel.workAddress.district = workDistrict.toString()
                viewModel.homeAddress.addressLine1 = homeAddressLine1.toString()
                viewModel.homeAddress.addressLine2 = homeAddressLine2.toString()
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Addresses",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 3/3",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .testTag("columnLayout")
        ) {
            AddressComposable(label = "Home Address", address = viewModel.homeAddress){

            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!viewModel.addWorkAddress) {
                OutlinedButton(
                    onClick = { viewModel.addWorkAddress = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add work address btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add work address icon")
                    Text(text = "Add a work address")
                }
            }

            if (viewModel.addWorkAddress) {
                AddressComposable(
                    label = "Work Address",
                    address = viewModel.workAddress
                ){
                    viewModel.workAddress.pincode = ""
                    viewModel.workAddress.state = ""
                    viewModel.workAddress.city = ""
                    viewModel.workAddress.addressLine2 = ""
                    viewModel.workAddress.addressLine1 = ""
                    viewModel.workAddress.district = ""
                    viewModel.addWorkAddress = false
                }
            }
            Spacer(modifier = Modifier.testTag("end of page"))
        }

        Button(
            onClick = {
                patientRegister.run {
                    homePostalCode = viewModel.homeAddress.pincode
                    homeAddressLine1 = viewModel.homeAddress.addressLine1
                    homeAddressLine2 = viewModel.homeAddress.addressLine2
                    homeState = viewModel.homeAddress.state
                    homeCity = viewModel.homeAddress.city
                    homeDistrict = viewModel.homeAddress.district
                    workPostalCode = viewModel.workAddress.pincode
                    workAddressLine1 = viewModel.workAddress.addressLine1
                    workAddressLine2 = viewModel.workAddress.addressLine2
                    workState = viewModel.workAddress.state
                    workCity = viewModel.workAddress.city
                    workDistrict = viewModel.workAddress.district
                }
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = "patient_register_details",
                    value = patientRegister
                )
                navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            enabled = viewModel.addressInfoValidation()
        ) {
            Text(text = "Submit & Preview")
        }
    }
}