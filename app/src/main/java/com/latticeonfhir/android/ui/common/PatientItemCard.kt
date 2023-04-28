package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.data.local.constants.Constants
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@Composable
fun PatientItemCard(navController: NavController, patient: PatientResponse) {
    val subtitle = "${patient.gender[0].uppercase()}/${Constants.GetAge(patient.birthDate)} · PID ${patient.fhirId}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .testTag("PATIENT")
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    patient
                )
                navController.navigate(Screen.PatientLandingScreen.route)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(8f)) {
            Text(
                text = Constants.GetFullName(patient.firstName, patient.middleName, patient.lastName),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Referred: 12 Jan",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}