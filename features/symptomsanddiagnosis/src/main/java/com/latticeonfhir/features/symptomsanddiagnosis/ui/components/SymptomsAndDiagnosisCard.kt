package com.latticeonfhir.features.symptomsanddiagnosis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.core.model.local.symdiag.SymptomsAndDiagnosisLocal
import com.latticeonfhir.core.utils.converters.TimeConverter.convertDateFormat
import com.latticeonfhir.core.utils.converters.TimeConverter.convertedDate
import com.latticeonfhir.features.symptomsanddiagnosis.R

@Composable
fun SymptomsAndDiagnosisCard(item: SymptomsAndDiagnosisLocal) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            .testTag("PREVIOUS_PRESCRIPTION_CARDS"),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null
                ) { expanded = !expanded }
                .testTag("PREVIOUS_PRESCRIPTION_TITLE_ROW"),
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = item.createdOn.convertedDate().convertDateFormat(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = item.practitionerName ?: stringResource(id = R.string.dashes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant

                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "DOWN_ARROW",
                    modifier = Modifier.rotate(rotationState)
                )
            }
            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier.testTag("PREVIOUS_PRESCRIPTION_EXPANDED_CARD")
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.symptoms),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (item.symptoms.isNotEmpty()) {
                        item.symptoms.forEach { symptoms ->
                            Text(
                                text = "• ${symptoms.display}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                            text = stringResource(id = R.string.dashes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.diagnosis),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (item.diagnosis.isNotEmpty()) {
                        item.diagnosis.forEach { diagnosis ->
                            Text(
                                text = "\u2022 ${diagnosis.code}, ${diagnosis.display}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                            text = stringResource(id = R.string.dashes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }


                }
            }
        }
    }
}

