package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.constants.States

@Composable
fun AddressComposable(label: String, address: Address, reset : () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()){
        if (label != "")
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        if (label == "Work Address"){
            IconButton(onClick = {
                reset()
            }) {
                Icon(Icons.Default.Clear, contentDescription = "disable work address")
            }
        }
    }
    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        val onlyNumbers = Regex("^\\d+\$")
        CustomTextField(
            value = address.pincode,
            label = "Postal Code *",
            weight = 0.4f,
            maxLength = 6,
            address.isPostalCodeValid,
            "Enter valid 6 digit postal code",
            KeyboardType.Number
        ) {
            if (it.matches(onlyNumbers) || it.isEmpty()) address.pincode = it
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
                        text = "State *",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("State *"),
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

            val statesList = States.getStateList()

            DropdownMenu(
                modifier = Modifier.fillMaxHeight(0.5f),
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
            ) {
                statesList.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = !expanded
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
        value = address.addressLine1,
        label = "Address Line 1 *",
        weight = 1f,
        maxLength = 150, address.isAddressLine1Valid,
        "Please enter your address.",
        KeyboardType.Text
    ) {
        address.addressLine1 = it
        address.isAddressLine1Valid = address.addressLine1.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.addressLine2,
        label = "Address Line 2",
        weight = 1f,
        maxLength = 150, false,
        "Enter valid input.",
        KeyboardType.Text
    ) {
        address.addressLine2 = it
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.city,
        label = "City *",
        weight = 1f,
        maxLength = 150, address.isCityValid,
        "Please enter your city.",
        KeyboardType.Text
    ) {
        address.city = it
        address.isCityValid = address.city.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.district,
        label = "District",
        weight = 1f,
        maxLength = 150, false,
        "Please enter your district.",
        KeyboardType.Text
    ) {
        address.district = it
    }
}