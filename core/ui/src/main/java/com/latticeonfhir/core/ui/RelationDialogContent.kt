package com.latticeonfhir.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RelationDialogContent(
    textBefore: String,
    textAfter: String,
    gender: String,
    relation: String,
    expanded: Boolean,
    updateRelation: (Boolean, String) -> Unit
) {
    Column {
        Text(
            textBefore,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(23.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "is the",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(10.dp))
            RelationsDropDown(
                gender,
                relation,
                expanded
            ) { update, value ->
                updateRelation(update, value)
            }

        }
        Spacer(modifier = Modifier.height(23.dp))
        Text(
            text = textAfter,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}