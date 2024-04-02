package com.latticeonfhir.android.ui.prescription.quickselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.medication.MedicationInfoConverter.getActiveIngredient
import java.util.Locale

@Composable
fun QuickSelectScreen(viewModel: PrescriptionViewModel = hiltViewModel()) {
    key(viewModel.selectedActiveIngredientsList) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("ACTIVE_INGREDIENT_LIST")
        ) {
            viewModel.activeIngredientsList.forEach { drug ->
                CompoundRow(activeIngredient = drug, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CompoundRow(activeIngredient: String, viewModel: PrescriptionViewModel) {
    val checkedState =
        remember { mutableStateOf(viewModel.selectedActiveIngredientsList.contains(activeIngredient)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                if (!checkedState.value) {
                    if (viewModel.selectedActiveIngredientsList.size < 10) {
                        viewModel.checkedActiveIngredient = activeIngredient
                    }
                } else {
                    viewModel.selectedActiveIngredientsList =
                        viewModel.selectedActiveIngredientsList - listOf(activeIngredient).toSet()
                    viewModel. medicationRequestAndMedicationList.forEach { medication ->
                        if (getActiveIngredient(medication.medication) == activeIngredient) {
                            viewModel. medicationRequestAndMedicationList =
                                viewModel. medicationRequestAndMedicationList - listOf(medication).toSet()
                        }
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.testTag("ACTIVE_INGREDIENT_CHECK_BOX"),
            checked = checkedState.value,
            onCheckedChange = {
                if (it) {
                    if (viewModel.selectedActiveIngredientsList.size < 10) {
                        viewModel.checkedActiveIngredient = activeIngredient
                    }
                } else {
                    viewModel.selectedActiveIngredientsList =
                        viewModel.selectedActiveIngredientsList - listOf(activeIngredient).toSet()
                    viewModel. medicationRequestAndMedicationList.forEach { medication ->
                        if (getActiveIngredient(medication.medication) == activeIngredient) {
                            viewModel. medicationRequestAndMedicationList =
                                viewModel. medicationRequestAndMedicationList - listOf(medication).toSet()
                        }
                    }
                }
            },
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = activeIngredient.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("ACTIVE_INGREDIENT_NAME")
            )
        }
    }
}