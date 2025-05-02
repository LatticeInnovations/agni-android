package com.latticeonfhir.features.vaccination.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE_ERROR_TYPE
import com.latticeonfhir.features.vaccination.R
import com.latticeonfhir.features.vaccination.data.enums.VaccineErrorTypeEnum

@Composable
fun VaccinationErrorScreen(
    navController: NavController
) {
    val errorType by remember {
        mutableStateOf(
            navController.previousBackStackEntry?.savedStateHandle?.get<String>(VACCINE_ERROR_TYPE)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(44.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            Icons.Default.Warning.name,
            tint = Color(0xFFDCCA3B),
            modifier = Modifier
                .size(48.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = if (errorType == VaccineErrorTypeEnum.DOSE.errorType) stringResource(R.string.vaccine_previous_dose_error_message)
            else stringResource(R.string.vaccine_time_window_error_message),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (errorType == VaccineErrorTypeEnum.DOSE.errorType) stringResource(R.string.vaccine_previous_dose_error_message_info)
            else stringResource(R.string.vaccine_time_window_error_message_info),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                navController.navigateUp()
            }
        ) {
            Text(stringResource(R.string.dismiss))
        }
    }
}