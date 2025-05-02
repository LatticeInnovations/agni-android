package com.latticeonfhir.core.symptomsanddiagnosis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionSelectionBottomSheet(
    label: String,
    list: Array<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
            )
            Icon(imageVector = Icons.Filled.Clear, contentDescription = "")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(list) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SheetPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = true) {

            OptionSelectionBottomSheet(
                label = "Head",
                list = arrayOf("Headeche", "pain", "throught")
            )
        }

    }
}
