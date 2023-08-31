package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.converters.responseconverter.States

@Composable
fun AddressComposable(label: String, address: Address) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (label != "")
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
    }
    Row(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        val onlyNumbers = Regex("^\\d+\$")
        CustomTextField(
            value = address.pincode,
            label = stringResource(id = R.string.postal_code),
            weight = 0.4f,
            maxLength = 6,
            address.isPostalCodeValid,
            stringResource(id = R.string.postal_code_error_msg),
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
                        text = stringResource(id = R.string.state),
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
                        Text(
                            text = stringResource(id = R.string.state_error_msg),
                            style = MaterialTheme.typography.bodySmall
                        )
                }
            )

            val statesList = States.getStateList()

            DropdownMenu(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .testTag("STATE_DROP_DOWN"),
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
        label = stringResource(id = R.string.address_line_1),
        weight = 1f,
        maxLength = 150, address.isAddressLine1Valid,
        stringResource(id = R.string.address_line_1_error_msg),
        KeyboardType.Text
    ) {
        address.addressLine1 = it
        address.isAddressLine1Valid = address.addressLine1.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.addressLine2,
        label = stringResource(id = R.string.address_line_2),
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
        label = stringResource(id = R.string.city),
        weight = 1f,
        maxLength = 150, address.isCityValid,
        stringResource(id = R.string.city_error_msg),
        KeyboardType.Text
    ) {
        address.city = it
        address.isCityValid = address.city.isEmpty()
    }
    Spacer(modifier = Modifier.height(15.dp))
    CustomTextField(
        value = address.district,
        label = stringResource(id = R.string.district),
        weight = 1f,
        maxLength = 150, false,
        stringResource(id = R.string.district_error_msg),
        KeyboardType.Text
    ) {
        address.district = it
    }
}