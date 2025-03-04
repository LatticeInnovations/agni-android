package com.latticeonfhir.android.ui.vaccination

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
import com.latticeonfhir.android.ui.theme.MissedContainer
import com.latticeonfhir.android.ui.theme.MissedLabel
import com.latticeonfhir.android.ui.theme.TakenContainer
import com.latticeonfhir.android.ui.theme.TakenLabel

@Composable
fun AllVaccinationScreen(
    viewModel: VaccinationViewModel
) {
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
        VaccineTimeComposable(stringResource(R.string.at_birth))
        VaccineTimeComposable("6 weeks")
        VaccineTimeComposable("10 weeks")
        VaccineTimeComposable("14 weeks")
        VaccineTimeComposable("6 months")
        VaccineTimeComposable("7 months")
        VaccineTimeComposable("6-9 months")
        VaccineTimeComposable("9 months")
        VaccineTimeComposable("12 months")
    }
}

@Composable
private fun VaccineTimeComposable(
    label: String
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
                text = stringResource(R.string.completed_vaccine_info, 2, 3),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.width(16.dp))
                VaccinationCard(isTaken = true)
                VaccinationCard()
                VaccinationCard(isDelayed = true)
                Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@Composable
private fun VaccinationCard(
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
        color = if (isTaken) TakenContainer
        else if (isDelayed) MissedContainer
        else MaterialTheme.colorScheme.surfaceBright
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Bacille Calmette-Guérin",
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
                        text = "BCG",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "1st dose",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight.name
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isTaken || isDelayed) {
                    if (isTaken) {
                        Icon(
                            painter = painterResource(R.drawable.check_circle),
                            contentDescription = "CHECK_ICON",
                            tint = TakenLabel,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.schedule),
                            contentDescription = "MISSED_ICON",
                            tint = MissedLabel,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = if (isTaken) stringResource(R.string.taken_on_date, "18 Jan 2024")
                    else if (isDelayed) stringResource(R.string.due_on_date, "18 Jan 2024")
                    else "24 Jan 2024",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTaken) TakenLabel
                    else if (isDelayed) MissedLabel
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}