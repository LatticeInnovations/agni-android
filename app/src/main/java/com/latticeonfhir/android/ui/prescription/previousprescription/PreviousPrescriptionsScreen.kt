package com.latticeonfhir.android.ui.prescription.previousprescription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.MedicineInfoConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import java.util.Locale

@Composable
fun PreviousPrescriptionsScreen(viewModel: PrescriptionViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (viewModel.previousPrescriptionList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No previous prescription.")
            }
        } else {
            viewModel.previousPrescriptionList.forEachIndexed { index, previousPrescription ->
                previousPrescription?.let { prescription ->
                    PrescriptionCard(viewModel, prescription, index == 0)
                }
            }
        }
    }
}

@Composable
fun PrescriptionCard(
    viewModel: PrescriptionViewModel,
    prescription: PrescriptionAndMedicineRelation,
    isLatest: Boolean
) {
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
            .padding(bottom = 20.dp),
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prescription.prescriptionEntity.prescriptionDate.toPrescriptionDate(),
                    style = MaterialTheme.typography.bodyLarge
                )
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
                Column {
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                    prescription.prescriptionDirectionAndMedicineView.forEach { directionAndMedication ->
                        MedicineDetails(
                            medName = directionAndMedication.medicationEntity.medName,
                            details = MedicineInfoConverter.getMedInfo(
                                duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                                frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                                medUnit = directionAndMedication.medicationEntity.medUnit,
                                timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                                note = directionAndMedication.prescriptionDirectionsEntity.note,
                                qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose
                            )
                        )

                    }
                    if (isLatest) {
                        TextButton(
                            onClick = {
                                viewModel.medicationsResponseWithMedicationList = emptyList()
                                viewModel.selectedActiveIngredientsList = emptyList()
                                prescription.prescriptionDirectionAndMedicineView.forEach { directionAndMedication ->
                                    viewModel.selectedActiveIngredientsList =
                                        viewModel.selectedActiveIngredientsList + listOf(
                                            directionAndMedication.medicationEntity.activeIngredient
                                        )
                                    viewModel.medicationsResponseWithMedicationList =
                                        viewModel.medicationsResponseWithMedicationList + listOf(
                                            MedicationResponseWithMedication(
                                                activeIngredient = directionAndMedication.medicationEntity.activeIngredient,
                                                medName = directionAndMedication.medicationEntity.medName,
                                                medUnit = directionAndMedication.medicationEntity.medUnit,
                                                medication = Medication(
                                                    doseForm = directionAndMedication.medicationEntity.doseForm,
                                                    duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                                                    qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose,
                                                    frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                                                    medFhirId = directionAndMedication.medicationEntity.medFhirId,
                                                    note = directionAndMedication.prescriptionDirectionsEntity.note,
                                                    timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                                                    qtyPrescribed = directionAndMedication.prescriptionDirectionsEntity.qtyPrescribed
                                                )
                                            )
                                        )
                                }
                                viewModel.bottomNavExpanded = false
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "Re-Prescribe")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetails(medName: String, details: String) {
    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Text(
            text = medName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}