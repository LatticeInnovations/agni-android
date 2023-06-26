package com.latticeonfhir.android.ui.prescription.filldetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillDetailsScreen(
    prescriptionViewModel: PrescriptionViewModel,
    viewModel: FillDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(prescriptionViewModel.checkedActiveIngredient) {
        viewModel.getMedicationByActiveIngredient(prescriptionViewModel.checkedActiveIngredient) {
            viewModel.formulationsList = it
        }
        viewModel.reset()
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (prescriptionViewModel.medicationToEdit != null) {
            viewModel.medSelected = prescriptionViewModel.medicationToEdit!!.medName
            viewModel.medUnit = prescriptionViewModel.medicationToEdit!!.medUnit
            viewModel.medDoseForm = prescriptionViewModel.medicationToEdit!!.medication.doseForm
            viewModel.quantityPerDose =
                prescriptionViewModel.medicationToEdit!!.medication.qtyPerDose.toString()
            viewModel.frequency =
                prescriptionViewModel.medicationToEdit!!.medication.frequency.toString()
            viewModel.notes = prescriptionViewModel.medicationToEdit!!.medication.note?:""
            viewModel.medFhirId = prescriptionViewModel.medicationToEdit!!.medication.medFhirId
            viewModel.timing = prescriptionViewModel.medicationToEdit!!.medication.timing?:""
            viewModel.duration =
                prescriptionViewModel.medicationToEdit!!.medication.duration.toString()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.fill_details),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        prescriptionViewModel.checkedActiveIngredient = ""
                        prescriptionViewModel.medicationToEdit = null
                        viewModel.reset()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    TextButton(
                        onClick = {
                            if (prescriptionViewModel.medicationToEdit != null) {
                                prescriptionViewModel.selectedActiveIngredientsList =
                                    prescriptionViewModel.selectedActiveIngredientsList - listOf(
                                        prescriptionViewModel.medicationToEdit!!.activeIngredient
                                    ).toSet()
                                prescriptionViewModel.medicationsResponseWithMedicationList =
                                    prescriptionViewModel.medicationsResponseWithMedicationList - listOf(
                                        prescriptionViewModel.medicationToEdit!!
                                    ).toSet()
                            }
                            prescriptionViewModel.selectedActiveIngredientsList =
                                prescriptionViewModel.selectedActiveIngredientsList + listOf(
                                    prescriptionViewModel.checkedActiveIngredient
                                )
                            prescriptionViewModel.medicationsResponseWithMedicationList =
                                prescriptionViewModel.medicationsResponseWithMedicationList + listOf(
                                    MedicationResponseWithMedication(
                                        medName = viewModel.medSelected,
                                        medUnit = viewModel.medUnit,
                                        activeIngredient = prescriptionViewModel.checkedActiveIngredient,
                                        medication = Medication(
                                            duration = viewModel.duration.toInt(),
                                            frequency = viewModel.frequency.toInt(),
                                            note = viewModel.notes,
                                            qtyPerDose = viewModel.quantityPerDose.toInt(),
                                            qtyPrescribed = viewModel.quantityPrescribed().toInt(),
                                            timing = viewModel.timing,
                                            doseForm = viewModel.medDoseForm,
                                            medFhirId = viewModel.medFhirId
                                        )
                                    )
                                )
                            prescriptionViewModel.checkedActiveIngredient = ""
                            prescriptionViewModel.medicationToEdit = null
                            viewModel.reset()
                        },
                        enabled = viewModel.quantityPrescribed().isNotBlank(),
                        modifier = Modifier.testTag("DONE_BTN")
                    ) {
                        Text(text = stringResource(id = R.string.done))
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        var formulationExpanded by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ACTIVE_INGREDIENT_FIELD"),
                            value = prescriptionViewModel.checkedActiveIngredient.capitalize(Locale.getDefault()),
                            onValueChange = {
                            },
                            label = {
                                Text(text = stringResource(id = R.string.active_ingredient))
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "DROP_DOWN_ARROW"
                                )
                            },
                            readOnly = true,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }.also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            formulationExpanded = !formulationExpanded
                                        }
                                    }
                                }
                            },
                            singleLine = true
                        )
                        DropdownMenu(
                            modifier = Modifier
                                .fillMaxHeight(0.6f)
                                .fillMaxWidth(0.9f)
                                .testTag("ACTIVE_INGREDIENT_DROPDOWN_LIST"),
                            expanded = formulationExpanded,
                            onDismissRequest = { formulationExpanded = !formulationExpanded },
                        ) {
                            prescriptionViewModel.activeIngredientsList.filter {
                                !prescriptionViewModel.selectedActiveIngredientsList.contains(it)
                            }.forEach { label ->
                                DropdownMenuItem(
                                    onClick = {
                                        formulationExpanded = !formulationExpanded
                                        prescriptionViewModel.checkedActiveIngredient = label
                                        viewModel.reset()
                                    },
                                    text = {
                                        Text(
                                            text = label.capitalize(Locale.getDefault()),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(id = R.string.formulations),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 15.dp)
                    )
                    FormulationRadioList(viewModel)
                    Spacer(modifier = Modifier.height(10.dp))
                    AnimatedVisibility(
                        visible = viewModel.medSelected != "",
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        FormulationsForm(prescriptionViewModel, viewModel)
                    }
                }
            }
        }
    )
}

@Composable
fun FormulationRadioList(viewModel: FillDetailsViewModel) {
    viewModel.formulationsList.forEach { formulation ->
        Row(
            Modifier
                .fillMaxWidth()
                .testTag("FORMULATION_LIST")
                .selectable(
                    selected = (formulation.medFhirId == viewModel.medFhirId),
                    onClick = {
                        viewModel.reset()
                        viewModel.medSelected = formulation.medName
                        viewModel.medUnit = formulation.medUnit
                        viewModel.medDoseForm = formulation.doseForm
                        viewModel.medFhirId = formulation.medFhirId
                    }
                )
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = formulation.medFhirId == viewModel.medFhirId,
                onClick = {
                    viewModel.reset()
                    viewModel.medSelected = formulation.medName
                    viewModel.medUnit = formulation.medUnit
                    viewModel.medDoseForm = formulation.doseForm
                    viewModel.medFhirId = formulation.medFhirId
                }
            )
            Text(
                text = formulation.medName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FormulationsForm(
    prescriptionViewModel: PrescriptionViewModel,
    viewModel: FillDetailsViewModel
) {
    Column {
        // quantity per dose
        Column {
            var quantityExpanded by remember {
                mutableStateOf(false)
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("QUANTITY_PER_DOSE"),
                value = viewModel.medUnit,
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.qty_per_dose))
                },
                leadingIcon = {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.quantityPerDose)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "DOWN_ARROW")
                    }
                },
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                interactionSource = remember {
                    MutableInteractionSource()
                }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                quantityExpanded = !quantityExpanded
                            }
                        }
                    }
                },
                singleLine = true
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxHeight(0.3f),
                expanded = quantityExpanded,
                onDismissRequest = { quantityExpanded = !quantityExpanded },
            ) {
                viewModel.qtyRange.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            quantityExpanded = !quantityExpanded
                            viewModel.quantityPerDose = label.toString()
                        },
                        text = {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // frequency
        Column {
            var freqExpanded by remember {
                mutableStateOf(false)
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("FREQUENCY"),
                value = stringResource(id = R.string.dose_per_day),
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.frequency))
                },
                leadingIcon = {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.frequency)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "DOWN_ARROW")
                    }
                },
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                interactionSource = remember {
                    MutableInteractionSource()
                }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                freqExpanded = !freqExpanded
                            }
                        }
                    }
                },
                singleLine = true
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxHeight(0.3f),
                expanded = freqExpanded,
                onDismissRequest = { freqExpanded = !freqExpanded },
            ) {
                viewModel.qtyRange.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            freqExpanded = !freqExpanded
                            viewModel.frequency = label.toString()
                        },
                        text = {
                            Text(
                                text = label.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // timing
        Column {
            var timingsExpanded by remember {
                mutableStateOf(false)
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("TIMING"),
                value = viewModel.timing,
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.timing_optional))
                },
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "DOWN_ARROW")
                },
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,

                interactionSource = remember {
                    MutableInteractionSource()
                }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                timingsExpanded = !timingsExpanded
                            }
                        }
                    }
                },
                singleLine = true
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(0.9f),
                expanded = timingsExpanded,
                onDismissRequest = { timingsExpanded = !timingsExpanded },
            ) {
                prescriptionViewModel.medicationDirectionsList.forEach { timing ->
                    DropdownMenuItem(
                        onClick = {
                            timingsExpanded = !timingsExpanded
                            viewModel.timing = timing.medicalDosage
                        },
                        text = {
                            Text(
                                text = timing.medicalDosage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Duration
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .testTag("DURATION"),
                value = viewModel.duration,
                onValueChange = {
                    if (it.matches(OnlyNumberRegex.onlyNumbers) && it != "0" && it.length<=3) viewModel.duration = it
                    else if (it.isEmpty()) viewModel.duration = it
                    viewModel.isDurationInvalid = viewModel.duration.isNotBlank() && viewModel.duration.toInt() > 180
                },
                label = {
                    Text(text = stringResource(id = R.string.duration_days))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                isError = viewModel.isDurationInvalid,
                supportingText = {
                    if (viewModel.isDurationInvalid)
                        Text(text = stringResource(id = R.string.duration_error_msg))
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Quantity prescribed
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .testTag("QUANTITY_PRESCRIBED")
                    .clickable(enabled = false) { },
                value = viewModel.quantityPrescribed(),
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.quantity_prescribed))
                },
                readOnly = true,
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // notes
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("NOTES"),
            value = viewModel.notes,
            onValueChange = {
                if (it.length <= 100) viewModel.notes = it
            },
            label = {
                Text(text = stringResource(id = R.string.notes_optional))
            }
        )
    }
}
