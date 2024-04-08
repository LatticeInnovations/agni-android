package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getLatticeId
import org.hl7.fhir.r4.model.Patient

@Composable
fun PatientItemCard(navController: NavController, patient: Patient) {
    val subtitle = "${patient.gender.display[0]}/${
        patient.birthDate.time.toAge()
    } · PID ${getLatticeId(patient)}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .testTag("PATIENT")
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    patient
                )
                navController.navigate(Screen.PatientLandingScreen.route)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(8f)) {
            Text(
                text = patient.nameFirstRep.nameAsSingleString,
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