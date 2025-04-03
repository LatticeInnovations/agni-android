package com.latticeonfhir.android.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


@Composable
fun AppointmentCompletedDialog(
    dismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        confirmButton = {
            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        text = {
            Text(text = stringResource(id = R.string.appointment_completed))
        }
    )
}