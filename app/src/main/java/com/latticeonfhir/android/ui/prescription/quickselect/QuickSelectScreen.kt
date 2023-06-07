package com.latticeonfhir.android.ui.prescription.quickselect

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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import androidx.lifecycle.viewmodel.compose.*
import java.util.Locale

@Composable
fun QuickSelectScreen(viewModel: PrescriptionViewModel = hiltViewModel()) {
    viewModel.getActiveIngredients()
    key(viewModel.selectedActiveIngredientsList) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.activeIngredientsList.forEach{ drug ->
                CompoundRow(drugName = drug, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CompoundRow(drugName: String, viewModel: PrescriptionViewModel) {
    val checkedState = remember { mutableStateOf(viewModel.selectedActiveIngredientsList.contains(drugName)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                if (it) {
                    viewModel.checkedActiveIngredient = drugName
                }
            },
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = drugName.capitalize(Locale.getDefault()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}