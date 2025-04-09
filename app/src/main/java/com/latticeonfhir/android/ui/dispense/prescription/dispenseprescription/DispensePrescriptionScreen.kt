package com.latticeonfhir.android.ui.dispense.prescription.dispenseprescription

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.core.R
import com.latticeonfhir.core.data.local.enums.DispenseStatusEnum
import com.latticeonfhir.android.theme.FullyDispensed
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispensePrescriptionScreen(
    navController: NavController,
    viewModel: DispensePrescriptionViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            val prescriptionId =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("prescription_id")
            viewModel.getData(prescriptionId!!)
            viewModel.isLaunched = true
        }
    }

    BackHandler {
        if (viewModel.medToEdit != null) viewModel.medToEdit = null
        else navController.navigateUp()
    }

    key(viewModel.recompose) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                Icons.AutoMirrored.Filled.ArrowBack.name
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(
                                R.string.prescribed_on,
                                viewModel.prescription?.prescription?.prescriptionDate?.toPrescriptionDate()
                                    ?: ""
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (viewModel.prescription?.dispensePrescriptionEntity?.status != DispenseStatusEnum.FULLY_DISPENSED.code) {
                        NotDispensedComposable(viewModel)
                    }
                    PreviousDispensedComposable(viewModel)
                }
            },
            bottomBar = {
                if (viewModel.selectedMedicine.isNotEmpty()) {
                    Button(
                        onClick = {
                            // dispense medicine
                            viewModel.dispenseMedication(
                                dispensed = {
                                    coroutineScope.launch {
                                        navController.navigateUp()
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .navigationBarsPadding()
                    ) {
                        Text(stringResource(R.string.dispense))
                    }
                }
            }
        )
    }
    if (viewModel.showAddNoteDialog) {
        AddNotesDialog(viewModel)
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            viewModel.medToEdit != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EditMedication(viewModel)
        }
    }
}

@Composable
private fun AddNotesDialog(viewModel: DispensePrescriptionViewModel) {
    var notes by remember { mutableStateOf(viewModel.dispenseNotes) }
    AlertDialog(
        onDismissRequest = { viewModel.showAddNoteDialog = false },
        confirmButton = {
            TextButton(onClick = {
                viewModel.dispenseNotes = notes.trim()
                viewModel.showAddNoteDialog = false
            }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                notes = ""
                viewModel.showAddNoteDialog = false
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.add_note))
        },
        text = {
            OutlinedTextField(
                value = notes,
                onValueChange = { value ->
                    if (value.length <= 100) notes = value
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        }
    )
}

@Composable
private fun PreviousDispensedComposable(viewModel: DispensePrescriptionViewModel) {
    if (viewModel.previousDispensed.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (viewModel.prescription?.dispensePrescriptionEntity?.status != DispenseStatusEnum.FULLY_DISPENSED.code) {
                Text(
                    text = stringResource(R.string.previous_dispense),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            viewModel.previousDispensed.forEach { medicine ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
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
                                    color = FullyDispensed.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = FullyDispensed,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.dispensed_on,
                                    medicine.dispenseDataEntity.generatedOn.toPrescriptionDate()
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = FullyDispensed,
                                modifier = Modifier.padding(
                                    vertical = 4.dp,
                                    horizontal = 10.dp
                                )
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        medicine.medicineDispenseList.forEach { med ->
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = viewModel.getMedNameFromMedFhirId(med.dispensedMedFhirId).medicationEntity.medName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${med.qtyDispensed} ${
                                        viewModel.getMedNameFromMedFhirId(
                                            med.dispensedMedFhirId
                                        ).medicationEntity.doseForm
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
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

@Composable
fun NotDispensedComposable(viewModel: DispensePrescriptionViewModel) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = DispenseStatusEnum.NOT_DISPENSED.display,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(
                                vertical = 8.dp,
                                horizontal = 10.dp
                            )
                        )
                    }
                    FilledTonalButton(
                        onClick = {
                            viewModel.showAddNoteDialog = true
                        }
                    ) {
                        Text(
                            if (viewModel.dispenseNotes.isBlank()) stringResource(R.string.add_note)
                            else stringResource(R.string.edit_note)
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                AnimatedVisibility(viewModel.dispenseNotes.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                R.string.dispense_notes,
                                viewModel.dispenseNotes
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                viewModel.medicationList.filter { it.qtyLeft > 0 }.forEach { medicine ->
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 6.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Checkbox(
                            modifier = Modifier.weight(0.1f),
                            checked = viewModel.selectedMedicine.map { it.medication.medicationEntity.medFhirId }
                                .contains(medicine.medication.medicationEntity.medFhirId),
                            onCheckedChange = { value ->
                                if (value) {
                                    viewModel.medicationList.forEach {
                                        if (it.medication == medicine.medication) {
                                            it.qtyToBeDispensed = it.qtyLeft
                                            viewModel.selectedMedicine.add(it)
                                        }
                                    }
                                } else {
                                    viewModel.medicationList.forEach {
                                        if (it.medication == medicine.medication) {
                                            it.qtyToBeDispensed = 0
                                            it.isModified = false
                                            it.note = ""
                                        }
                                    }
                                    viewModel.selectedMedicine.removeIf {
                                        it.medication.medicationEntity.medFhirId == medicine.medication.medicationEntity.medFhirId
                                    }
                                }
                                viewModel.recompose = !viewModel.recompose
                            }
                        )
                        Column(
                            modifier = Modifier
                                .weight(0.7f)
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = medicine.medication.medicationEntity.medName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${if (!medicine.isModified) medicine.qtyLeft else medicine.qtyToBeDispensed} ${medicine.medication.medicationEntity.doseForm}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedVisibility(medicine.note.isNotBlank()) {
                                Text(
                                    text = stringResource(
                                        R.string.notes_dispense,
                                        medicine.note
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                viewModel.medToEdit = medicine
                            },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                Icons.Outlined.Edit.name,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedication(viewModel: DispensePrescriptionViewModel) {
    if (viewModel.medToEdit != null) {
        var tempQuantity by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var qtyError by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.medToEdit = null
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                Icons.AutoMirrored.Filled.ArrowBack.name
                            )
                        }
                    },
                    title = {
                        Text(text = stringResource(R.string.edit_medicine))
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                viewModel.medicationList.forEach { med ->
                                    if (med.medication == viewModel.medToEdit!!.medication) {
                                        med.qtyToBeDispensed = tempQuantity.toInt()
                                        med.note = notes
                                        med.isModified = true
                                        viewModel.selectedMedicine.removeIf { selectedMed ->
                                            selectedMed.medication.medicationEntity.medFhirId == viewModel.medToEdit!!.medication.medicationEntity.medFhirId
                                        }
                                        viewModel.selectedMedicine.add(med)
                                        viewModel.recompose = !viewModel.recompose
                                    }
                                }
                                viewModel.medToEdit = null
                            },
                            enabled = tempQuantity.isNotBlank() && notes.isNotBlank() && tempQuantity.toInt() != viewModel.medToEdit!!.qtyLeft && !qtyError
                        ) {
                            Text(stringResource(R.string.done))
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = viewModel.medToEdit!!.medication.medicationEntity.medName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                R.string.quantity_prescribed_info,
                                viewModel.medToEdit!!.medication.prescriptionDirectionsEntity.qtyPrescribed
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = stringResource(
                                R.string.quantity_dispensed_info,
                                viewModel.medToEdit!!.medication.prescriptionDirectionsEntity.qtyPrescribed - viewModel.medToEdit!!.qtyLeft
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempQuantity,
                        onValueChange = {
                            if (it.isBlank()) tempQuantity = ""
                            else if (it.matches(onlyNumbers) && it.length < 4 && it != "0")
                                tempQuantity = it
                            if (tempQuantity.isNotBlank()) qtyError =
                                tempQuantity.toInt() >= viewModel.medToEdit!!.qtyLeft
                        },
                        label = {
                            Text(stringResource(R.string.qty_to_dispense))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = qtyError,
                        supportingText = {
                            if (qtyError) {
                                Text(stringResource(R.string.edit_qty_error_message))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            if (it.length <= 100) notes = it
                        },
                        label = {
                            Text(stringResource(R.string.notes_label))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}