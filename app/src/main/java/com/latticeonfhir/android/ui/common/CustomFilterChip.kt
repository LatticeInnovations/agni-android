package com.latticeonfhir.android.ui.common

import androidx.compose.material3.*
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