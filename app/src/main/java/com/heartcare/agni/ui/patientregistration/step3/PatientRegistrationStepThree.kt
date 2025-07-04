package com.heartcare.agni.ui.patientregistration.step3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.AddressComposable
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationStepThree(
    navController: NavController,
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepThreeViewModel = viewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
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
        viewModel.isLaunched = true
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
                text = stringResource(id = R.string.addresses),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 3/${patientRegistrationViewModel.totalSteps}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (patientRegistrationViewModel.fromHouseholdMember) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = viewModel.checkedState,
                        onCheckedChange = { value ->
                            viewModel.checkedState = value
                            if (value) {
                                viewModel.homeAddress.apply {
                                    pincode =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.postalCode
                                    state =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.state
                                    addressLine1 =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.addressLine1
                                    addressLine2 =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.addressLine2
                                            ?: ""
                                    district =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.district
                                            ?: ""
                                    city =
                                        patientRegistrationViewModel.patientFrom!!.permanentAddress.city
                                }
                            } else {
                                viewModel.homeAddress.apply {
                                    pincode = ""
                                    state = ""
                                    addressLine1 = ""
                                    addressLine2 = ""
                                    district = ""
                                    city = ""
                                }
                            }
                        }
                    )
                }
                Text(
                    text = stringResource(R.string.same_address_as_source_patient),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .testTag("columnLayout")
        ) {
            AddressComposable(
                label = null,
                address = viewModel.homeAddress
            )
            Spacer(modifier = Modifier.testTag("end of page"))
        }

        Button(
            onClick = {
                patientRegister.run {
                    homePostalCode = viewModel.homeAddress.pincode
                    homeAddressLine1 = viewModel.homeAddress.addressLine1.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    homeAddressLine2 = viewModel.homeAddress.addressLine2.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    homeState = viewModel.homeAddress.state
                    homeCity = viewModel.homeAddress.city.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    homeDistrict = viewModel.homeAddress.district.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    workPostalCode = viewModel.workAddress.pincode
                    workAddressLine1 = viewModel.workAddress.addressLine1.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    workAddressLine2 = viewModel.workAddress.addressLine2.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    workState = viewModel.workAddress.state
                    workCity = viewModel.workAddress.city.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    workDistrict = viewModel.workAddress.district.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = "patient_register_details",
                    value = patientRegister
                )
                if (patientRegistrationViewModel.fromHouseholdMember) {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "fromHouseholdMember",
                        value = patientRegistrationViewModel.fromHouseholdMember
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "patientFrom",
                        value = patientRegistrationViewModel.patientFrom
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "relation",
                        value = patientRegistrationViewModel.relation
                    )
                }
                navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            enabled = viewModel.addressInfoValidation()
        ) {
            Text(text = stringResource(id = R.string.submit_and_preview))
        }
    }
}