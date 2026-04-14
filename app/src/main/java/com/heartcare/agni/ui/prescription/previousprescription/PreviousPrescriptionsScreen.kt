package com.heartcare.agni.ui.prescription.previousprescription

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication
import com.heartcare.agni.ui.prescription.PrescriptionViewModel
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import com.heartcare.agni.utils.converters.responseconverter.medication.MedicationInfoConverter.getMedInfo
import com.heartcare.agni.utils.converters.responseconverter.toMedicationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PreviousPrescriptionsScreen(
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    viewModel: PrescriptionViewModel = hiltViewModel(),
    onRequireWizard: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (viewModel.previousPrescriptionList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_previous_prescription))
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                viewModel.previousPrescriptionList.forEachIndexed { index, previousPrescription ->
                    PrescriptionCard(
                        viewModel,
                        previousPrescription,
                        index == 0
                                && !(isToday(previousPrescription.prescriptionEntity.prescriptionDate))
                                && viewModel.patient!!.patientDeceasedReason.isNullOrBlank(),
                        snackBarHostState,
                        coroutineScope,
                        pagerState,
                        onRequireWizard
                    )
                }
            }
        }
    }
}

@Composable
fun PrescriptionCard(
    viewModel: PrescriptionViewModel,
    prescription: PrescriptionAndMedicineRelation,
    showRePrescribeBtn: Boolean,
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    onRequireWizard: () -> Unit
) {
    val context = LocalContext.current
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded }
                    .testTag("PREVIOUS_PRESCRIPTION_TITLE_ROW"),
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
                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    if (prescription.prescriptionDirectionAndMedicineView.isEmpty()) {
                        Text(
                            text = stringResource(R.string.dash),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        prescription.prescriptionDirectionAndMedicineView.forEach { directionAndMedication ->
                            MedicineDetails(
                                medName = directionAndMedication.medicationEntity.medName,
                                brandName = directionAndMedication.prescriptionDirectionsEntity.brandName,
                                codeCategoryClass = "${directionAndMedication.medicationEntity.medCodeName} · ${directionAndMedication.medicationEntity.categoryName} · ${directionAndMedication.medicationEntity.className}",
                                details = getMedInfo(
                                    duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                                    frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                                    medUnit = directionAndMedication.medicationEntity.doseForm.lowercase(),
                                    timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                                    note = directionAndMedication.prescriptionDirectionsEntity.note,
                                    qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose,
                                    qtyPrescribed = directionAndMedication.prescriptionDirectionsEntity.qtyPrescribed,
                                    context = context
                                )
                            )
                        }
                        if (showRePrescribeBtn) {
                            TextButton(
                                onClick = {
                                    // re prescribe
                                    rePrescribeBtnClick(
                                        viewModel = viewModel,
                                        coroutineScope = coroutineScope,
                                        snackBarHostState = snackBarHostState,
                                        context = context,
                                        prescription = prescription,
                                        onRequireWizard = onRequireWizard
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("RE_PRESCRIBE_BTN")
                            ) {
                                Text(text = stringResource(R.string.re_precribe))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun rePrescribeBtnClick(
    viewModel: PrescriptionViewModel,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context,
    prescription: PrescriptionAndMedicineRelation,
    onRequireWizard: () -> Unit
) {
    if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
        viewModel.isReprescribing = true
        viewModel.represcribingPrescription = prescription
        onRequireWizard()
    } else {
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                context.getString(R.string.patient_deceased_error_msg)
            )
        }
    }
}

@Composable
fun MedicineDetails(
    medName: String,
    brandName: String? = null,
    codeCategoryClass: String,
    details: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = medName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = codeCategoryClass,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        brandName?.let {
            Text(
                text = stringResource(R.string.brand_name, it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun saveRePrescription(
    context: Context,
    viewModel: PrescriptionViewModel,
    prescription: PrescriptionAndMedicineRelation,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState
) {
    viewModel.medicationsResponseWithMedicationList = emptyList()
    viewModel.selectedMedicationsList = emptyList()
    prescription.prescriptionDirectionAndMedicineView.forEach { directionAndMedication ->
        viewModel.selectedMedicationsList += listOf(
            directionAndMedication.medicationEntity.toMedicationResponse()
        )
        viewModel.medicationsResponseWithMedicationList += listOf(
            MedicationResponseWithMedication(
                medicationResponse = directionAndMedication.medicationEntity.toMedicationResponse(),
                medication = Medication(
                    doseForm = directionAndMedication.medicationEntity.doseForm,
                    duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                    qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose,
                    frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                    medFhirId = directionAndMedication.medicationEntity.medFhirId,
                    note = directionAndMedication.prescriptionDirectionsEntity.note,
                    timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                    qtyPrescribed = directionAndMedication.prescriptionDirectionsEntity.qtyPrescribed,
                    medReqUuid = UUIDBuilder.generateUUID(),
                    medReqFhirId = null,
                    brandName = directionAndMedication.prescriptionDirectionsEntity.brandName,
                    doseFormCode = directionAndMedication.prescriptionDirectionsEntity.doseFormCode
                )
            )
        )
    }
    viewModel.bottomNavExpanded = false
    coroutineScope.launch {
        pagerState.animateScrollToPage(1)
        snackBarHostState.showSnackbar(
            message = context.getString(R.string.re_prescribed_successfully)
        )
    }
}