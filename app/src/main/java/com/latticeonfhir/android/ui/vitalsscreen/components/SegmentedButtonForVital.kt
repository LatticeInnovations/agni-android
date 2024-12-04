package com.latticeonfhir.android.ui.vitalsscreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonForVital(
    options: List<String>,
    selectedOption: String,
    isCVDListEmpty: Boolean = true,
    onOptionSelected: (String) -> Unit
) {

    Column(modifier = Modifier
        .padding(start = 16.dp, top = 8.dp)
        .width(230.dp)) {

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = SegmentedButtonDefaults.colors(
                        disabledInactiveBorderColor = MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.12f
                        ),
                        disabledInactiveContentColor = MaterialTheme.colorScheme.outline,
                        activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledInactiveContainerColor = Color.Transparent
                    ),
                    enabled = !(option == options[1] && isCVDListEmpty),
                    border = BorderStroke(
                        if (option == options[1] && isCVDListEmpty) 0.3.dp else 1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = options.size
                    )
                ) {
                    Text(text = option)
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun SegmentedButtonExamplePreview() {
    var selectedOption by remember { mutableStateOf("CVD record") }
    SegmentedButtonForVital(options = listOf("All", "CVD record"),
        isCVDListEmpty = false,
        selectedOption = selectedOption,
        onOptionSelected = { selectedOption = it })
}

