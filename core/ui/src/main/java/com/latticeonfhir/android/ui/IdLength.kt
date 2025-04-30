package com.latticeonfhir.android.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IdLength(idName: String, requiredLength: Int, tag: String) {
    Text(
        text = "${idName.length}/$requiredLength",
        textAlign = TextAlign.Right,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, end = 15.dp)
            .testTag(tag),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}