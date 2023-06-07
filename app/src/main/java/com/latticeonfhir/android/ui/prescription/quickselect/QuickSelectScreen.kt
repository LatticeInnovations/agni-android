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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import androidx.lifecycle.viewmodel.compose.*

@Composable
fun QuickSelectScreen(viewModel: PrescriptionViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        viewModel.compoundList.forEach{drug ->
            CompoundRow(drugName = drug, viewModel = viewModel)
        }
    }
}

@Composable
fun CompoundRow(drugName: String, viewModel: PrescriptionViewModel) {
    val checkedState = remember { mutableStateOf(viewModel.selectedCompoundList.contains(drugName)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                if (it) {
                    viewModel.checkedCompound = drugName
                }
            },
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = drugName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}