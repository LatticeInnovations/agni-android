package com.heartcare.agni.ui.dispense.otc

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.constants.NavControllerConstants.OTC_DISPENSED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTCScreen(
    navController: NavController,
    viewModel: OTCViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getOTCMedications()
        }
        viewModel.isLaunched = true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.otc),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // add otc
                            viewModel.dispenseMedication {
                                coroutineScope.launch {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        OTC_DISPENSED,
                                        true
                                    )
                                    navController.navigateUp()
                                }
                            }
                        },
                        enabled = viewModel.selectedMedicine != null && viewModel.qtyPrescribed.isNotBlank() && !viewModel.isError
                    ) {
                        Text(stringResource(R.string.done))
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActiveIngredientDropDown(viewModel)
                if (viewModel.selectedMedicine != null) {
                    FormulationsForm(viewModel)
                }
            }
        }
    )
}

@Composable
private fun FormulationsForm(viewModel: OTCViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.formulations),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = viewModel.selectedMedicine!!.medicationEntity.medName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp)
        )
        OutlinedTextField(
            value = viewModel.qtyPrescribed,
            onValueChange = { value ->
                if (value.isBlank()) {
                    viewModel.qtyPrescribed = ""
                    viewModel.isError = true
                }
                else if (value.matches(onlyNumbers) && value.length < 3) {
                    viewModel.qtyPrescribed = value
                    viewModel.isError = viewModel.qtyPrescribed.toInt() !in 1..50
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.quantity_prescribed)+"*")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = viewModel.isError,
            supportingText = {
                if (viewModel.isError) {
                    Text(stringResource(R.string.otc_quantity_error_message))
                }
            }
        )
        OutlinedTextField(
            value = viewModel.notes,
            onValueChange = { value ->
                if (value.length <= 100) viewModel.notes = value
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.notes_optional))
            }
        )
    }
}

@Composable
private fun ActiveIngredientDropDown(viewModel: OTCViewModel) {
    Column {
        var expanded by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = viewModel.selectedMedicine?.medicationEntity?.medName ?: "",
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.medicine_name))
            },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    Icons.Default.KeyboardArrowDown.name
                )
            },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            expanded = !expanded
                        }
                    }
                }
            },
            singleLine = true
        )
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("ACTIVE_INGREDIENT_DROPDOWN_LIST"),
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            viewModel.allMedications.forEach { medication ->
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                        viewModel.selectedMedicine = medication
                    },
                    text = {
                        Text(
                            text = medication.medicationEntity.medName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    contentPadding = PaddingValues(10.dp)
                )
            }
        }
    }
}
