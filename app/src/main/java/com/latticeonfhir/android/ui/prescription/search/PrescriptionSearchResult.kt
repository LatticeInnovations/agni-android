package com.latticeonfhir.android.ui.prescription.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import com.latticeonfhir.android.ui.prescription.quickselect.CompoundRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionSearchResult(viewModel: PrescriptionViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.match_found, "6"), style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.isSearchResult = false }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Search, contentDescription = "SEARCH_ICON")
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)){
                Column {
                    viewModel.activeIngredientsList.forEach{ activeIngredient ->
                        CompoundRow(activeIngredient = activeIngredient, viewModel = viewModel)
                    }
                }
            }
        }
    )
}