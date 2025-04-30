package com.latticeonfhir.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.converters.TimeConverter.toAge
import com.latticeonfhir.core.utils.converters.TimeConverter.toTimeInMilli
import com.latticeonfhir.core.utils.converters.responseconverter.NameConverter.getFullName

@Composable
fun CancelAppointmentDialog(
    patient: PatientResponse,
    dateAndTime: String,
    closeDialog: (Boolean) -> Unit
) {
    val subtitle = "${patient.gender[0].uppercase()}/${
        patient.birthDate.toTimeInMilli().toAge()
    }, PID: ${patient.fhirId}"
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(id = R.string.cancel_appointment),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("DIALOG_TITLE")
            )
        },
        text = {
            Column {
                Text(
                    text = getFullName(
                        patient.firstName,
                        patient.middleName,
                        patient.lastName
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = dateAndTime,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // delete appointment
                    closeDialog(true)
                },
                modifier = Modifier.testTag("POSITIVE_BTN")
            ) {
                Text(
                    stringResource(id = R.string.yes_i_want_to_cancel)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog(false)
                },
                modifier = Modifier.testTag("NEGATIVE_BTN")
            ) {
                Text(
                    stringResource(id = R.string.no_go_back)
                )
            }
        }
    )
}