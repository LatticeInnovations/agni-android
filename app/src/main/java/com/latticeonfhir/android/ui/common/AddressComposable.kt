package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.states.getBlockNames
import com.latticeonfhir.android.utils.states.getDistrictNames
import com.latticeonfhir.android.utils.states.getStateNames

@Composable
fun AddressComposable(
    label: String?,
    address: Address,
    isSearching: Boolean = false
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        label?.let {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        StateDropDown(address, isSearching, getStateNames(context))
        DistrictDropDown(address, isSearching, getDistrictNames(context, address.state))
        BlockDropDown(address, isSearching, getBlockNames(context, address.state, address.district))
        City(address)
        AddressLineOne(address)
        AddressLineTwo(address)
    }
}

private fun Address.clearDistrict() {
    district = ""
    isDistrictValid = false
}

private fun Address.clearBlock() {
    block = ""
    isBlockValid = false
}

private fun Address.clearStateDependents() {
    clearDistrict()
    clearBlock()
}

@Composable
private fun StateDropDown(
    address: Address,
    isSearching: Boolean,
    states: List<String>
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    SearchableDropdown(
        value = address.state,
        items = states,
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            address.isStateValid = states.none { state ->
                state == address.state
            }

            address.clearStateDependents()
        },
        onValueChange = { query ->
            address.state = query
            address.isStateValid = states.none {
                it.equals(query, ignoreCase = true)
            }

            address.clearStateDependents()
        },
        onItemSelected = { selected ->
            address.state = selected
            address.isStateValid = false

            address.clearStateDependents()
        },
        label = if (isSearching) {
            stringResource(R.string.state)
        } else {
            stringResource(R.string.state_mandatory)
        },
        isError = address.isStateValid && !isSearching,
        errorMessage = stringResource(R.string.state_error_msg)
    )
}

@Composable
private fun DistrictDropDown(
    address: Address,
    isSearching: Boolean,
    districts: List<String>
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    SearchableDropdown(
        value = address.district,
        items = districts,
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            address.isDistrictValid = districts.none { state ->
                state.equals(address.district, ignoreCase = true)
            }

            address.clearBlock()
        },
        onValueChange = { query ->
            address.district = query
            address.isDistrictValid = districts.none { state ->
                state.equals(address.district, ignoreCase = true)
            }

            address.clearBlock()
        },
        onItemSelected = { selected ->
            address.district = selected
            address.isDistrictValid = false

            address.clearBlock()
        },
        label = if (isSearching) {
            stringResource(R.string.district)
        } else {
            stringResource(R.string.district_mandatory)
        },
        isError = address.isDistrictValid && !isSearching,
        errorMessage = stringResource(R.string.district_error_msg),
        enabled = (!address.isStateValid && address.state.isNotBlank()) || isSearching
    )
}

@Composable
private fun BlockDropDown(
    address: Address,
    isSearching: Boolean,
    districts: List<String>
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    SearchableDropdown(
        value = address.block,
        items = districts,
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        onValueChange = { query ->
            address.block = query
        },
        onItemSelected = { selected ->
            address.block = selected
        },
        label = stringResource(R.string.block),
        isError = false,
        errorMessage = "",
        enabled = (!address.isDistrictValid && address.district.isNotBlank()) || isSearching
    )
}

@Composable
private fun AddressLineOne(
    address: Address
) {
    CustomTextField(
        value = address.addressLine1,
        label = stringResource(id = R.string.address_line_1),
        weight = 1f,
        maxLength = 150,
        isError = false,
        error = "",
        KeyboardType.Text,
        KeyboardCapitalization.Words
    ) {
        address.addressLine1 = it
    }
}

@Composable
private fun AddressLineTwo(
    address: Address
) {
    CustomTextField(
        value = address.addressLine2,
        label = stringResource(id = R.string.address_line_2),
        weight = 1f,
        maxLength = 150, false,
        "Enter valid input.",
        KeyboardType.Text,
        KeyboardCapitalization.Words
    ) {
        address.addressLine2 = it
    }
}

@Composable
private fun City(
    address: Address
) {
    CustomTextField(
        value = address.city,
        label = stringResource(id = R.string.city),
        weight = 1f,
        maxLength = 150,
        isError = false,
        error = "",
        KeyboardType.Text,
        KeyboardCapitalization.Words
    ) {
        address.city = it
    }
}
