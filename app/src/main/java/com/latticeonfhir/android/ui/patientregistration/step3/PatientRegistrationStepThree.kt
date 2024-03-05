package com.latticeonfhir.android.ui.patientregistration.step3

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.AddressComposable
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.theme.Neutral40
import com.latticeonfhir.android.utils.constants.NavControllerConstants.FROM_HOUSEHOLD_MEMBER
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_FROM
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT_REGISTER_DETAILS
import com.latticeonfhir.android.utils.constants.NavControllerConstants.RELATION
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.StringType
import java.util.Locale

@Composable
fun PatientRegistrationStepThree(
    navController: NavController,
    patientRegistrationViewModel: PatientRegistrationViewModel,
    viewModel: PatientRegistrationStepThreeViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            if (patientRegistrationViewModel.isEditing) viewModel.setData(patientRegistrationViewModel.patient)
            viewModel.isLaunched = true
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
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .testTag("columnLayout")
        ) {
            AddressComposable(
                label = stringResource(id = R.string.home_address),
                address = viewModel.homeAddress
            )
            Spacer(modifier = Modifier.testTag("end of page"))
        }

        Button(
            onClick = {
                patientRegistrationViewModel.patient.apply {
                    address.clear()
                    address.add(
                        Address().apply {
                            use = Address.AddressUse.HOME
                            postalCode = viewModel.homeAddress.pincode
                            district = viewModel.homeAddress.district.replaceFirstChar {
                                it.titlecase(Locale.getDefault())
                            }
                            state = viewModel.homeAddress.state
                            city = viewModel.homeAddress.city.replaceFirstChar {
                                it.titlecase(Locale.getDefault())
                            }
                            line.add(
                                StringType(
                                    viewModel.homeAddress.addressLine1.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    }
                                )
                            )
                            line.add(
                                StringType(
                                    viewModel.homeAddress.addressLine2.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    }
                                )
                            )
                            text = "${viewModel.homeAddress.addressLine1} ${viewModel.homeAddress.addressLine2}"
                            country = "India"
                        }
                    )
                }
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = PATIENT_REGISTER_DETAILS,
                    value = patientRegistrationViewModel.patient
                )
                if (patientRegistrationViewModel.fromHouseholdMember) {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = FROM_HOUSEHOLD_MEMBER,
                        value = patientRegistrationViewModel.fromHouseholdMember
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = PATIENT_FROM,
                        value = patientRegistrationViewModel.patientFrom
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = RELATION,
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