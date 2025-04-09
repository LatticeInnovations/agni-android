package com.latticeonfhir.android.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.latticeonfhir.core.R


@Composable
fun DiscardAllRelationDialog(discard: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = {
            discard(false)
        },
        title = {
            Text(
                text = stringResource(id = R.string.discard_relations_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("delete dialog title")
            )
        },
        text = {
            Text(
                stringResource(id = R.string.discard_relations_dialog_description),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    discard(true)
                }) {
                Text(
                    stringResource(id = R.string.yes_discard)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    discard(false)
                }) {
                Text(
                    stringResource(id = R.string.no_go_back)
                )
            }
        }
    )
}