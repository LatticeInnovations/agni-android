package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.ui.common.Detail
import com.latticeonfhir.android.ui.common.Label
import com.latticeonfhir.android.ui.theme.Primary10
import com.latticeonfhir.android.ui.theme.SyncFailedColor

@Composable
fun ProfileScreen(viewModel: LandingScreenViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Label(stringResource(id = R.string.name_label))
                Detail(detail = viewModel.userName, tag = "NAME")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.role_label))
                Detail(detail = viewModel.userRole, tag = "ROLE")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.phone_number_label))
                Detail(detail = "+91 ${viewModel.userPhoneNo}", tag = "PHONE_NO")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.email))
                Detail(detail = viewModel.userEmail, tag = "EMAIL")
            }
        }

        SyncStatusView(viewModel)
    }
}

@Composable
fun SyncStatusView(viewModel: LandingScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.sync_status), style = MaterialTheme.typography.titleLarge)
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.synced),
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = viewModel.lastSyncDate,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                if (viewModel.syncIconDisplay != 0) SyncStatusChip(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun SyncStatusChip(viewModel: LandingScreenViewModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (viewModel.syncStatusDisplay) {
                SyncStatusMessageEnum.SYNCING_FAILED.display -> {
                    SyncFailedColor
                }
                SyncStatusMessageEnum.SYNCING_COMPLETED.display -> {
                    MaterialTheme.colorScheme.primaryContainer
                }
                else -> {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            }
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 10.dp, end = 12.dp, bottom = 5.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = viewModel.syncIconDisplay),
                contentDescription = "",
                tint = setTextAndIconColor(viewModel = viewModel)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = viewModel.syncStatusDisplay,
                color = setTextAndIconColor(viewModel = viewModel),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

}

@Composable
private fun setTextAndIconColor(viewModel: LandingScreenViewModel): Color {
    return when (viewModel.syncStatus) {
        WorkerStatus.FAILED -> {
            Primary10
        }
        else -> {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

}

