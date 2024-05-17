package com.latticeonfhir.android.ui.appointments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.ui.theme.ArrivedContainer
import com.latticeonfhir.android.ui.theme.ArrivedLabel
import com.latticeonfhir.android.ui.theme.CancelledContainer
import com.latticeonfhir.android.ui.theme.CancelledLabel
import com.latticeonfhir.android.ui.theme.CompletedContainer
import com.latticeonfhir.android.ui.theme.CompletedLabel
import com.latticeonfhir.android.ui.theme.InProgressContainer
import com.latticeonfhir.android.ui.theme.InProgressLabel
import com.latticeonfhir.android.ui.theme.NoShowContainer
import com.latticeonfhir.android.ui.theme.NoShowLabel
import com.latticeonfhir.android.ui.theme.TodayScheduledContainer
import com.latticeonfhir.android.ui.theme.TodayScheduledLabel
import com.latticeonfhir.android.ui.theme.WalkInContainer
import com.latticeonfhir.android.ui.theme.WalkInLabel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentDate

@Composable
fun CompletedAppointments(viewModel: AppointmentsScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        viewModel.pastAppointmentsList.forEach { appointmentResponse ->
            CompletedAppointmentCard(appointmentResponse)
        }
    }
}

@Composable
fun CompletedAppointmentCard(appointmentResponseLocal: AppointmentResponseLocal) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appointmentResponseLocal.slot.start.toAppointmentDate(),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        StatusChip(appointmentResponseLocal)
    }
}

@Composable
private fun StatusChip(appointmentResponseLocal: AppointmentResponseLocal) {
    AssistChip(
        onClick = {},
        label = {
            Text(text = AppointmentStatusEnum.fromValue(appointmentResponseLocal.status).label)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when (appointmentResponseLocal.status) {
                AppointmentStatusEnum.WALK_IN.value -> WalkInContainer
                AppointmentStatusEnum.ARRIVED.value -> ArrivedContainer
                AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledContainer
                AppointmentStatusEnum.CANCELLED.value -> CancelledContainer
                AppointmentStatusEnum.COMPLETED.value -> CompletedContainer
                AppointmentStatusEnum.IN_PROGRESS.value -> InProgressContainer
                else -> NoShowContainer
            },
            labelColor = when (appointmentResponseLocal.status) {
                AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
                AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
                AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = when (appointmentResponseLocal.status) {
                AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
                AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
                AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
                else -> NoShowLabel
            }
        )
    )
}