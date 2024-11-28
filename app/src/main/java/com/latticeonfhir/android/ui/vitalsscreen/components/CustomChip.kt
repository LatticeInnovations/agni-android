package com.latticeonfhir.android.ui.vitalsscreen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun CustomChip(
    idSelected: Boolean,
    label: String,
    shape: Shape = ShapeDefaults.Large,
    updateSelection: () -> Unit
) {
    FilterChip(
        selected = idSelected,
        onClick = {
            updateSelection()
        },
        label = { Text(text = label, modifier = Modifier.padding(4.dp)) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = idSelected,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            selectedBorderWidth = 1.dp,
        ),
        shape = shape,
        modifier = Modifier.testTag("$label chip")
    )
}

@Preview(showBackground = true)
@Composable
private fun PRec() {
    CustomChip(idSelected = true, label = "HR") {

    }

}