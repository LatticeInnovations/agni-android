package com.latticeonfhir.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CustomDialog(
    canBeDismissed: Boolean = true,
    title: String,
    text: String,
    dismissBtnText: String?,
    confirmBtnText: String,
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (canBeDismissed) dismiss() },
        confirmButton = {
            TextButton(onClick = { confirm() }) {
                Text(text = confirmBtnText)
            }
        },
        dismissButton = {
            dismissBtnText?.let { dismissBtnText ->
                TextButton(onClick = { dismiss() }) {
                    Text(text = dismissBtnText)
                }
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        }
    )
}