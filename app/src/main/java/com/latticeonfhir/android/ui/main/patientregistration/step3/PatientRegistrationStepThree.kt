package com.latticeonfhir.android.ui.main.patientregistration

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
import com.latticeonfhir.android.ui.main.patientregistration.step3.PatientRegistrationStepThreeViewModel
import com.latticeonfhir.android.ui.main.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.main.patientregistration.model.PatientRegister
import com.latticeonfhir.android.ui.main.patientregistration.step3.Address

@Composable
fun PatientRegistrationStepThree(navController: NavController, patientRegister: PatientRegister, viewModel: PatientRegistrationStepThreeViewModel = viewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.homeAddress.pincode = homePostalCode.toString()
                viewModel.homeAddress.state = homeState.toString()
                viewModel.homeAddress.city = homeCity.toString()
                viewModel.homeAddress.area = homeArea.toString()
                viewModel.homeAddress.town = homeTown.toString()
                viewModel.addWorkAddress = workPostalCode.toString().isNotEmpty()
                viewModel.workAddress.pincode = workPostalCode.toString()
                viewModel.workAddress.state = workState.toString()
                viewModel.workAddress.city = workCity.toString()
                viewModel.workAddress.area = workArea.toString()
                viewModel.workAddress.town = workTown.toString()
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
            AddressComposable(label = "Home Address", address = viewModel.homeAddress, viewModel)

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
                    address = viewModel.workAddress,
                    viewModel
                )
            }
            Spacer(modifier = Modifier.testTag("end of page"))
        }

        Button(
            onClick = {
                patientRegister.run {
                    homePostalCode = viewModel.homeAddress.pincode
                    homeArea = viewModel.homeAddress.area
                    homeState = viewModel.homeAddress.state
                    homeCity = viewModel.homeAddress.city
                    homeTown = viewModel.homeAddress.town
                    workPostalCode = viewModel.workAddress.pincode
                    workArea = viewModel.workAddress.area
                    workState = viewModel.workAddress.state
                    workCity = viewModel.workAddress.city
                    workTown = viewModel.workAddress.town
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
            Text(text = "Submit")
        }
    }
}

@Composable
fun AddressComposable(label: String, address: Address, viewModel: PatientRegistrationStepThreeViewModel) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()){
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (label == "Work Address"){
            IconButton(onClick = {
                address.pincode = ""
                address.state = ""
                address.city = ""
                address.area = ""
                address.town = ""
                viewModel.addWorkAddress = false
            }) {
                Icon(Icons.Default.Clear, contentDescription = "disable work address")
            }
        }
    }
    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        CustomTextField(
            value = address.pincode,
            label = "Postal Code",
            weight = 0.4f,
            maxLength = 6,
            address.isPostalCodeValid,
            "Enter valid 6 digit postal code"
        ) {
            address.pincode = it
            address.isPostalCodeValid = address.pincode.length < 6
        }
        Spacer(modifier = Modifier.width(15.dp))
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = address.state,
                onValueChange = {
                    address.isStateValid = address.state == ""
                },
                label = {
                    Text(
                        text = "State",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("State"),
                readOnly = true,
                interactionSource = remember {
                    MutableInteractionSource()
                }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                isError = address.isStateValid,
                supportingText = {
                    if (address.isStateValid)
                        Text(text = "Please select a state.", style = MaterialTheme.typography.bodySmall)
                }
            )
            DropdownMenu(
                modifier = Modifier.fillMaxHeight(0.5f),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                viewModel.statesList.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            address.state = label
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
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.area,
        label = "House No., Building, Street, Area",
        weight = 1f,
        maxLength = 150, address.isAreaValid,
        "Enter valid input."
    ) {
        address.area = it
        address.isAreaValid = address.area.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.town,
        label = "Town/ Locality",
        weight = 1f,
        maxLength = 150, address.isTownValid,
        "Enter valid input."
    ) {
        address.town = it
        address.isTownValid = address.town.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.city,
        label = "City/ District",
        weight = 1f,
        maxLength = 150, address.isCityValid,
        "Enter valid input."
    ) {
        address.city = it
        address.isCityValid = address.city.isEmpty()
    }
}