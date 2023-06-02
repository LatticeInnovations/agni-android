package com.latticeonfhir.android.ui.prescription.filldetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillDetailsScreen(prescriptionViewModel: PrescriptionViewModel, viewModel: FillDetailsViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.fill_details), style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { prescriptionViewModel.checkedCompound = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    TextButton(onClick = {
                        prescriptionViewModel.selectedCompoundList.add(prescriptionViewModel.checkedCompound)
                        prescriptionViewModel.checkedCompound = ""
                    }) {
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
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = prescriptionViewModel.checkedCompound,
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
                        readOnly = true
                    )
                    Text(
                        text = stringResource(id = R.string.formulations),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 15.dp)
                    )
                    FormulationRadioList(viewModel)
                    AnimatedVisibility(
                        visible = viewModel.formulationSelected.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        FormulationsForm(viewModel)
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
                .selectable(
                    selected = (formulation == viewModel.formulationSelected),
                    onClick = { viewModel.formulationSelected = formulation }
                )
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = formulation == viewModel.formulationSelected,
                onClick = { viewModel.formulationSelected = formulation }
            )
            Text(
                text = formulation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FormulationsForm(viewModel: FillDetailsViewModel) {
    Column {
        // quantity per dose
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(id = R.string.ml),
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
            textStyle = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        // frequency
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
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
            textStyle = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))

        // timing
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.timing,
            onValueChange = {},
            label = {
                Text(text = stringResource(id = R.string.timing_optional))
            },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "DOWN_ARROW")
            },
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Duration
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = viewModel.duration,
                onValueChange = {
                    viewModel.duration = it
                },
                label = {
                    Text(text = stringResource(id = R.string.duration_days))
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Quantity prescribed
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = viewModel.quantityPrescribed,
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.quantity_prescribed))
                },
                readOnly = true
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // notes
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.notes,
            onValueChange = {
                viewModel.notes = it
            },
            label = {
                Text(text = stringResource(id = R.string.notes_optional))
            }
        )
    }
}
