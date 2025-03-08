package com.latticeonfhir.android.ui.vaccination.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.ui.theme.MissedContainer
import com.latticeonfhir.android.ui.theme.MissedContainerDark
import com.latticeonfhir.android.ui.theme.MissedLabel
import com.latticeonfhir.android.ui.theme.MissedLabelDark
import com.latticeonfhir.android.ui.theme.TakenContainer
import com.latticeonfhir.android.ui.theme.TakenContainerDark
import com.latticeonfhir.android.ui.theme.TakenLabel
import com.latticeonfhir.android.ui.theme.TakenLabelDark
import com.latticeonfhir.android.ui.vaccination.AgeComposable
import com.latticeonfhir.android.ui.vaccination.VaccinationViewModel
import com.latticeonfhir.android.ui.vaccination.utils.VaccinesUtils.categorizeVaccines
import com.latticeonfhir.android.ui.vaccination.utils.VaccinesUtils.getNumberWithOrdinalIndicator
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertStringToDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import java.util.Date
import java.util.Locale

@Composable
fun AllVaccinationScreen(
    viewModel: VaccinationViewModel
) {
    viewModel.patient?.let { patient ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (isSystemInDarkTheme()) Color.Black else Color.White
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AgeComposable(viewModel)
            viewModel.immunizationRecommendationList.categorizeVaccines(
                dob = patient.birthDate.convertStringToDate()
            ).forEach { (label, listOfVaccines) ->
                VaccineTimeComposable(label, listOfVaccines.sortedBy { it.vaccineStartDate })
            }
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Composable
private fun VaccineTimeComposable(
    label: String,
    listOfVaccines: List<ImmunizationRecommendation>
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                null
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.completed_vaccine_info, 0, listOfVaccines.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AnimatedVisibility(
            visible = isExpanded
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.width(16.dp))
                listOfVaccines.forEach { vaccine ->
                    VaccinationCard(
                        vaccine = vaccine,
                        isTaken = vaccine.takenOn != null,
                        isDelayed = vaccine.vaccineStartDate.toEndOfDay() < Date().time
                    )
                }
                Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@Composable
private fun VaccinationCard(
    vaccine: ImmunizationRecommendation,
    isTaken: Boolean = false,
    isDelayed: Boolean = false
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    // navigate to vaccination view screen
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = getColorOfContainer(isTaken, isDelayed)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = vaccine.name.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = vaccine.shortName.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.number_dose, vaccine.doseNumber.getNumberWithOrdinalIndicator()),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight.name,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isTaken || isDelayed) {
                    Icon(
                        painter = if (isTaken) painterResource(R.drawable.check_circle)
                        else painterResource(R.drawable.schedule),
                        contentDescription = null,
                        tint = getColorOfLabel(isTaken, isDelayed),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = if (isTaken) stringResource(
                        R.string.taken_on_date,
                        vaccine.takenOn!!.toPrescriptionDate()
                    )
                    else if (isDelayed) stringResource(
                        R.string.due_on_date,
                        vaccine.vaccineStartDate.toPrescriptionDate()
                    )
                    else vaccine.vaccineStartDate.toPrescriptionDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = getColorOfLabel(isTaken, isDelayed)
                )
            }
        }
    }
}

@Composable
private fun getColorOfContainer(isTaken: Boolean, isDelayed: Boolean): Color {
    return when (isSystemInDarkTheme()) {
        true -> {
            if (isTaken) TakenContainerDark
            else if (isDelayed) MissedContainerDark
            else MaterialTheme.colorScheme.surfaceBright
        }

        false -> {
            if (isTaken) TakenContainer
            else if (isDelayed) MissedContainer
            else MaterialTheme.colorScheme.surfaceBright
        }
    }
}

@Composable
private fun getColorOfLabel(isTaken: Boolean, isDelayed: Boolean): Color {
    return when (isSystemInDarkTheme()) {
        true -> {
            if (isTaken) TakenLabelDark
            else if (isDelayed) MissedLabelDark
            else MaterialTheme.colorScheme.onSurface
        }

        false -> {
            if (isTaken) TakenLabel
            else if (isDelayed) MissedLabel
            else MaterialTheme.colorScheme.onSurface
        }
    }
}
