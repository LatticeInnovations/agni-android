package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableDropdown(
    value: String,
    items: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    val filteredItems = remember(value, items) {
        if (value.isBlank()) {
            items
        } else {
            items.filter {
                it.contains(value, ignoreCase = true)
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange(!expanded)
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                onExpandedChange(true)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    MenuAnchorType.PrimaryEditable,
                    enabled = enabled
                ),
            enabled = enabled,
            singleLine = true,
            label = {
                Text(label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            isError = isError,
            supportingText = if (isError && !errorMessage.isNullOrBlank()) {
                {
                    Text(errorMessage)
                }
            }
             else null
        )

        ExposedDropdownMenu(
            expanded = expanded && filteredItems.isNotEmpty(),
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(item)
                    },
                    onClick = {
                        onItemSelected(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}