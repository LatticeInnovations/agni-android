package com.latticeonfhir.android.ui.main.patientregistration

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.ui.theme.Neutral10
import com.latticeonfhir.android.ui.main.ui.theme.Neutral40
import com.latticeonfhir.android.ui.main.ui.theme.Primary40

@Composable
fun PatientRegistrationStepThree(viewModel: PatientRegistrationViewModel) {
    val context = LocalContext.current
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 3/3",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .weight(1f)) {
            AddressComposable(label = "Home Address", address = viewModel.homeAddress)

            Spacer(modifier = Modifier.height(20.dp))

            if (!viewModel.addWorkAddress) {
                OutlinedButton(
                    onClick = { viewModel.addWorkAddress = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(text = "Add a work address")
                }
            }

            if (viewModel.addWorkAddress) {
                AddressComposable(label = "Work Address", address = viewModel.workAddress)
            }
        }

        Button(
            onClick = {
                viewModel.step = 4
                Log.d("manseeyy","${viewModel.firstName}" +
                        "\n${viewModel.middleName}" +
                        "\n${viewModel.lastName}" +
                        "\n${viewModel.dob}" +
                        "\n${viewModel.phoneNumber}" +
                        "\n${viewModel.email}" +
                        "\n${viewModel.gender}" +
                        "\n${viewModel.passportId}" +
                        "\n${viewModel.voterId}" +
                        "\n${viewModel.patientId}" +
                        "\n${viewModel.homeAddress.pincode}"+
                        "\n${viewModel.homeAddress.state}"+
                        "\n${viewModel.homeAddress.area}"+
                        "\n${viewModel.homeAddress.town}"+
                        "\n${viewModel.homeAddress.city}"+
                        "\n${viewModel.workAddress.pincode}"+
                        "\n${viewModel.workAddress.state}"+
                        "\n${viewModel.workAddress.area}"+
                        "\n${viewModel.workAddress.town}"+
                        "\n${viewModel.workAddress.city}")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressComposable(label: String, address: Address) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        CustomTextField(
            value = address.pincode,
            label = "Postal Code",
            weight = 0.4f,
            maxLength = 6
        ) {
            address.pincode = it
        }
        Spacer(modifier = Modifier.width(15.dp))
        OutlinedTextField(
            value = address.state,
            onValueChange = {},
            placeholder = { Text(text = "State",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)},
            modifier = Modifier.fillMaxWidth().padding(top=8.dp),
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary40
            )
        )
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.area,
        label = "House No., Building, Street, Area",
        weight = 1f,
        maxLength = 150
    ) {
        address.area = it
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.town,
        label = "Town/ Locality",
        weight = 1f,
        maxLength = 150
    ) {
        address.town = it
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.city,
        label = "City/ District",
        weight = 1f,
        maxLength = 150
    ) {
        address.city = it
    }
}