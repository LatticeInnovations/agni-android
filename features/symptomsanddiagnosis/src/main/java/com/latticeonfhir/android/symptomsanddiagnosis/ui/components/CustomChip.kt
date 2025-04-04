package com.latticeonfhir.android.symptomsanddiagnosis.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SymptomsCustomChip(
    modifier: Modifier = Modifier,
    idSelected: Boolean,
    label: String,
    shape: Shape = ShapeDefaults.Small,
    icon: ImageVector? = null,
    updateSelection: () -> Unit
) {
    FilterChip(
        selected = idSelected,
        onClick = {
            updateSelection()
        },
        label = { Text(text = label, modifier = Modifier.padding(4.dp)) },
        shape = shape,
        modifier = modifier.testTag("$label chip"),
        trailingIcon = {
            if (icon != null && idSelected)
                Icon(
                    icon,
                    contentDescription = null,
                )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PRec() {
    SymptomsCustomChip(idSelected = true, label = "Stomach pain", icon = Icons.Default.Clear) {

    }

}