package com.heartcare.agni.ui.dispense.prescription

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.ui.dispense.DrugDispenseViewModel
import com.heartcare.agni.ui.prescription.previousprescription.MedicineDetails
import com.heartcare.agni.utils.converters.responseconverter.medication.MedicationInfoConverter.getMedInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRXScreen(
    viewModel: DrugDispenseViewModel
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.view_rx))
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.prescriptionSelected = null
                        }
                    ) {
                        Icon(Icons.Default.Clear, Icons.Default.Clear.name)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                viewModel.prescriptionSelected?.prescriptionDirectionAndMedicineView?.forEach { directionAndMedication ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        MedicineDetails(
                            medName = directionAndMedication.medicationEntity.medName,
                            details = getMedInfo(
                                duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                                frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                                medUnit = directionAndMedication.medicationEntity.medUnit,
                                timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                                note = directionAndMedication.prescriptionDirectionsEntity.note,
                                qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose,
                                qtyPrescribed = directionAndMedication.prescriptionDirectionsEntity.qtyPrescribed,
                                context = context
                            )
                        )
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    )
}