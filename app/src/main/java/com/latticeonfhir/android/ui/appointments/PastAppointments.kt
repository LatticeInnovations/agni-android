package com.latticeonfhir.core.ui.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.theme.ArrivedContainer
import com.latticeonfhir.android.theme.ArrivedLabel
import com.latticeonfhir.android.theme.CancelledContainer
import com.latticeonfhir.android.theme.CancelledLabel
import com.latticeonfhir.core.theme.CompletedContainer
import com.latticeonfhir.core.theme.CompletedLabel
import com.latticeonfhir.core.theme.InProgressContainer
import com.latticeonfhir.core.theme.InProgressLabel
import com.latticeonfhir.core.theme.NoShowContainer
import com.latticeonfhir.android.theme.NoShowLabel
import com.latticeonfhir.core.theme.TodayScheduledContainer
import com.latticeonfhir.core.theme.TodayScheduledLabel
import com.latticeonfhir.core.theme.WalkInContainer
import com.latticeonfhir.core.theme.WalkInLabel
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toAppointmentDate

@Composable
fun PastAppointments(viewModel: AppointmentsScreenViewModel) {
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
    Surface(
        content = {
            Text(
                text = AppointmentStatusEnum.fromValue(appointmentResponseLocal.status).label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelLarge
            )
        },
        color = when (appointmentResponseLocal.status) {
            AppointmentStatusEnum.WALK_IN.value -> WalkInContainer
            AppointmentStatusEnum.ARRIVED.value -> ArrivedContainer
            AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledContainer
            AppointmentStatusEnum.CANCELLED.value -> CancelledContainer
            AppointmentStatusEnum.COMPLETED.value -> CompletedContainer
            AppointmentStatusEnum.IN_PROGRESS.value -> InProgressContainer
            else -> NoShowContainer
        },
        contentColor = when (appointmentResponseLocal.status) {
            AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
            AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
            AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
            AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
            AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
            AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = when (appointmentResponseLocal.status) {
                AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
                AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
                AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
                else -> NoShowLabel
            }
        ),
        shape = RoundedCornerShape(8.dp)
    )
}