package com.latticeonfhir.features.dispense.ui.prescription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.core.database.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.core.model.enums.DispenseStatusEnum
import com.latticeonfhir.core.model.enums.DispenseStatusEnum.Companion.codeToDisplay
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.theme.FullyDispensed
import com.latticeonfhir.core.theme.PartiallyDispensed
import com.latticeonfhir.core.utils.converters.TimeConverter.toPrescriptionDate
import com.latticeonfhir.features.dispense.R
import com.latticeonfhir.features.dispense.data.enums.DispenseCategoryEnum
import com.latticeonfhir.features.dispense.ui.DrugDispenseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PrescriptionTabScreen(
    navController: NavController,
    viewModel: DrugDispenseViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    if (viewModel.previousPrescriptionList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_prescription_found)
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
            viewModel.previousPrescriptionList.forEach { prescription ->
                DispensePrescriptionCard(
                    prescription = prescription,
                    viewRxClicked = {
                        viewModel.prescriptionSelected = prescription
                    },
                    dispenseBtnClicked = {
                        dispenseBtnClickHandle(viewModel, prescription, coroutineScope, navController)
                    }
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

private fun dispenseBtnClickHandle(
    viewModel: DrugDispenseViewModel,
    prescription: DispenseAndPrescriptionRelation,
    coroutineScope: CoroutineScope,
    navController: NavController
) {
    if (prescription.dispensePrescriptionEntity.status == DispenseStatusEnum.FULLY_DISPENSED.code) {
        coroutineScope.launch {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "prescription_id",
                prescription.prescription.id
            )
            navController.navigate(Screen.DispensePrescriptionScreen.route)
        }
    } else {
        viewModel.getAppointmentInfo(
            callback = {
                if (viewModel.canAddDispense) {
                    coroutineScope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "prescription_id",
                            prescription.prescription.id
                        )
                        navController.navigate(Screen.DispensePrescriptionScreen.route)
                    }
                } else if (viewModel.isAppointmentCompleted) {
                    viewModel.showAppointmentCompletedDialog = true
                } else {
                    viewModel.prescriptionToDispense = prescription
                    viewModel.categoryClicked =
                        DispenseCategoryEnum.PRESCRIBED.value
                    viewModel.showAddToQueueDialog = true
                }
            }
        )
    }
}

@Composable
private fun DispensePrescriptionCard(
    prescription: DispenseAndPrescriptionRelation,
    viewRxClicked: () -> Unit,
    dispenseBtnClicked: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = prescription.prescription.prescriptionDate.toPrescriptionDate(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = getStatusChipBackgroundColor(prescription.dispensePrescriptionEntity.status),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = getStatusTextAndBorderColor(prescription.dispensePrescriptionEntity.status),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = codeToDisplay(prescription.dispensePrescriptionEntity.status),
                        style = MaterialTheme.typography.labelLarge,
                        color = getStatusTextAndBorderColor(prescription.dispensePrescriptionEntity.status),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                prescription.prescriptionDirectionAndMedicineView.forEach { medicine ->
                    Text(
                        text = medicine.medicationEntity.medName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewRxClicked()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.view_rx),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                val color = MaterialTheme.colorScheme.outlineVariant
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (prescription.dispensePrescriptionEntity.status == DispenseStatusEnum.FULLY_DISPENSED.code) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.secondaryContainer
                        )
                        .clickable {
                            dispenseBtnClicked()
                        }
                        .drawBehind {
                            drawLine(
                                color = color,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (prescription.dispensePrescriptionEntity.status == DispenseStatusEnum.FULLY_DISPENSED.code) stringResource(
                            R.string.see_details
                        )
                        else stringResource(R.string.dispense),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getStatusTextAndBorderColor(status: String): Color {
    return when (status) {
        DispenseStatusEnum.NOT_DISPENSED.code -> MaterialTheme.colorScheme.error
        DispenseStatusEnum.PARTIALLY_DISPENSED.code -> PartiallyDispensed
        DispenseStatusEnum.FULLY_DISPENSED.code -> FullyDispensed
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun getStatusChipBackgroundColor(status: String): Color {
    return when (status) {
        DispenseStatusEnum.NOT_DISPENSED.code -> MaterialTheme.colorScheme.errorContainer
        DispenseStatusEnum.PARTIALLY_DISPENSED.code -> PartiallyDispensed.copy(alpha = 0.12f)
        DispenseStatusEnum.FULLY_DISPENSED.code -> FullyDispensed.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }
}
