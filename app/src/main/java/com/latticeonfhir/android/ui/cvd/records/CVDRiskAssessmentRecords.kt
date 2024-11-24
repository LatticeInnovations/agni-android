package com.latticeonfhir.android.ui.cvd.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.ui.cvd.CVDRiskAssessmentViewModel
import com.latticeonfhir.android.ui.theme.Black
import com.latticeonfhir.android.ui.theme.White
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toddMMMyyyy

@Composable
fun CVDRiskAssessmentRecords(
    viewModel: CVDRiskAssessmentViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.getRecords()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (viewModel.previousRecords.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = if (isSystemInDarkTheme()) Black else White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.graph_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            // line chart
        }
        if (viewModel.previousRecords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = stringResource(R.string.no_record_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 120.dp)
                )
            }
        } else {
            Text(
                text = stringResource(R.string.total_records),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            viewModel.previousRecords.forEach { record ->
                RecordDetailsComposable(
                    record = record,
                    onClick = {
                        viewModel.selectedRecord = record
                    }
                )
            }
        }
    }
}

@Composable
private fun RecordDetailsComposable(
    record: CVDResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.percentage, record.risk.toString()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = record.createdOn.toddMMMyyyy(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
