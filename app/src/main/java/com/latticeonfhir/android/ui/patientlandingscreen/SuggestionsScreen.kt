package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.*

@Composable
fun SuggestionsScreen(viewModel: SuggestionsScreenViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "Here are patients with similar addresses or nearby locations.",
            style = MaterialTheme.typography.bodyLarge
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(bottom = 20.dp)
        ) {
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)
            SuggestedMembersCard(viewModel)

        }
    }
    if (viewModel.showConnectDialog){

    }
}

@Composable
fun SuggestedMembersCard(viewModel: SuggestionsScreenViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clickable {
                viewModel.showConnectDialog = true
            },
        shape = RoundedCornerShape(1.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Prashant Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "M/32",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "A-45, Rajendra Nagar, Delhi\n" +
                        "+91-99000-88222 · PID 12345",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}