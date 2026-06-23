package com.latticeonfhir.android.ui.common

import android.content.Context
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
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers
import com.latticeonfhir.android.utils.states.getBlockNames
import com.latticeonfhir.android.utils.states.getDistrictNames
import com.latticeonfhir.android.utils.states.getStateAndDistrictByPincode
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
        BlockDropDown(address, getBlockNames(context, address.state, address.district))
        City(address)
        AddressLineOne(address)
        AddressLineTwo(address)
        PostalCode(address, context)
    }
}

private fun Address.clearDistrict() {
    district = ""
    isDistrictValid = false
}

private fun Address.clearBlock() {
    block = ""
    isBlockValid = false

    clearPincode()
}

private fun Address.clearPincode() {
    pincode = ""
    isPostalCodeValid = false
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
        },
        onValueChange = { query ->
            if (address.state != query) {
                address.state = query
                address.isStateValid = states.none {
                    it.equals(query, ignoreCase = true)
                }

                address.clearStateDependents()
            }
        },
        onItemSelected = { selected ->
            if (address.state != selected) {
                address.state = selected
                address.isStateValid = false

                address.clearStateDependents()
            }
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
        },
        onValueChange = { query ->
            if (address.district != query) {
                address.district = query
                address.isDistrictValid = districts.none { state ->
                    state.equals(address.district, ignoreCase = true)
                }

                address.clearBlock()
            }
        },
        onItemSelected = { selected ->
            if (address.district != selected) {
                address.district = selected
                address.isDistrictValid = false

                address.clearBlock()
            }
        },
        label = if (isSearching) {
            stringResource(R.string.district)
        } else {
            stringResource(R.string.district_mandatory)
        },
        isError = address.isDistrictValid && !isSearching,
        errorMessage = stringResource(R.string.district_error_msg),
        enabled = !address.isStateValid && address.state.isNotBlank()
    )
}

@Composable
private fun BlockDropDown(
    address: Address,
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
        enabled = !address.isDistrictValid && address.district.isNotBlank()
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

@Composable
private fun PostalCode(
    address: Address,
    context: Context
) {
    CustomTextField(
        value = address.pincode,
        label = stringResource(id = R.string.postal_code),
        weight = 1f,
        maxLength = 6,
        isError = address.isPostalCodeValid,
        error = stringResource(R.string.postal_code_error_msg),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.isEmpty() || it.matches(onlyNumbers)) {
            address.pincode = it
            address.isPostalCodeValid = it.isNotEmpty() && it.length != 6
        }
        if (address.pincode.length == 6) {
            getStateAndDistrictByPincode(context, it)?.let { (state, district) ->
                address.state = state
                address.district = district
                address.block = ""
            }
        }
    }
}
