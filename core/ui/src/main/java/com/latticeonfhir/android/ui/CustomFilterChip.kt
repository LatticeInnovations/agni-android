package com.latticeonfhir.android.ui

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun CustomFilterChip(
    selector: String,
    selected: String,
    label: String,
    updateSelected: (String) -> Unit
) {
    FilterChip(
        selected = selector == selected,
        modifier = Modifier.testTag(selected),
        onClick = { updateSelected(selected) },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selector == selected,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            selectedBorderWidth = 1.dp
        )
    )
}