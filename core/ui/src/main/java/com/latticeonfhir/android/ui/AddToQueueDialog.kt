package com.latticeonfhir.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun AddToQueueDialog(
    appointment: AppointmentResponseLocal?,
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    CustomDialog(
        title = if (appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
            id = R.string.add_to_queue_question
        ),
        text = stringResource(id = R.string.add_to_queue_dialog_description),
        dismissBtnText = stringResource(id = R.string.dismiss),
        confirmBtnText = if (appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
            id = R.string.add_to_queue
        ),
        dismiss = { dismiss() },
        confirm = { confirm() }
    )
}