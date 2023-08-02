package com.latticeonfhir.android.ui.appointments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentDate

@Composable
fun CompletedAppointments(viewModel: AppointmentsScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        viewModel.completedAppointmentsList.forEach { appointmentResponse ->
            CompletedAppointmentCard(appointmentResponse)
        }
    }
}

@Composable
fun CompletedAppointmentCard(appointmentResponse: AppointmentResponse) {
    Row(
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Text(
            text = appointmentResponse.slot.start.toAppointmentDate(),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}