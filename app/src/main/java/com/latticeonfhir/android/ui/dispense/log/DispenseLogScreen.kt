package com.latticeonfhir.core.ui.dispense.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.core.R
import com.latticeonfhir.core.data.local.enums.DispenseCategoryEnum
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.core.ui.dispense.DrugDispenseViewModel
import com.latticeonfhir.core.theme.OTC
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionDate

@Composable
fun DispenseLogScreen(
    viewModel: DrugDispenseViewModel
) {
    if (viewModel.previousDispensed.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_dispense_records)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(1.dp))
            viewModel.previousDispensed.forEach { dispensedPrescriptionInfo ->
                DispenseLogCard(viewModel, dispensedPrescriptionInfo)
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
private fun DispenseLogCard(
    viewModel: DrugDispenseViewModel,
    dispensedPrescriptionInfo: DispensedPrescriptionInfo
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = dispensedPrescriptionInfo.dispenseDataEntity.generatedOn.toPrescriptionDate(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            if (!dispensedPrescriptionInfo.dispenseDataEntity.note.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            R.string.notes_dispense,
                            dispensedPrescriptionInfo.dispenseDataEntity.note
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                dispensedPrescriptionInfo.medicineDispenseList.forEach { medicine ->
                    if (medicine.isModified) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.modified_prescription),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = viewModel.getMedNameFromMedFhirId(medicine.dispensedMedFhirId).medicationEntity.medName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${medicine.qtyDispensed} ${
                                        viewModel.getMedNameFromMedFhirId(
                                            medicine.dispensedMedFhirId
                                        ).medicationEntity.doseForm
                                    }",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                if (!medicine.medNote.isNullOrBlank()) {
                                    Text(
                                        text = stringResource(
                                            R.string.notes_dispense,
                                            medicine.medNote
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.prescribed),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = viewModel.getMedNameFromMedFhirId(medicine.prescribedMedFhirId).medicationEntity.medName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "${medicine.qtyPrescribed} ${
                                        viewModel.getMedNameFromMedFhirId(
                                            medicine.prescribedMedFhirId
                                        ).medicationEntity.doseForm
                                    }",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = viewModel.getMedNameFromMedFhirId(medicine.dispensedMedFhirId).medicationEntity.medName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (medicine.category == DispenseCategoryEnum.OTC.value) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp, top = 4.dp)
                                            .background(
                                                shape = RoundedCornerShape(8.dp),
                                                color = OTC.copy(alpha = 0.12f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = OTC,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = stringResource(R.string.otc),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = OTC,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 10.dp
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "${medicine.qtyDispensed} ${
                                        viewModel.getMedNameFromMedFhirId(
                                            medicine.dispensedMedFhirId
                                        ).medicationEntity.doseForm
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (!medicine.medNote.isNullOrBlank()) {
                                Text(
                                    text = stringResource(
                                        R.string.notes_dispense,
                                        medicine.medNote
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}