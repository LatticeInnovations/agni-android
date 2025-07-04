package com.heartcare.agni.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.data.local.enums.WorkerStatus
import com.heartcare.agni.ui.theme.Primary10
import com.heartcare.agni.ui.theme.SyncFailedColor

@Composable
fun DisplaySyncStatus(
    syncStatus: WorkerStatus,
    syncIcon: Int,
    syncStatusMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(23.dp)
            .background(
                color = when (syncStatus) {
                    WorkerStatus.IN_PROGRESS -> {
                        MaterialTheme.colorScheme.surfaceVariant
                    }

                    WorkerStatus.FAILED -> {
                        SyncFailedColor
                    }

                    WorkerStatus.SUCCESS -> {
                        MaterialTheme.colorScheme.primaryContainer
                    }

                    WorkerStatus.OFFLINE -> {
                        MaterialTheme.colorScheme.outlineVariant
                    }

                    else -> {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (syncIcon != 0) Icon(
                painter = painterResource(id = syncIcon),
                contentDescription = "",
                tint = setTextAndIconColor(syncStatus)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = syncStatusMessage,
                color = setTextAndIconColor(syncStatus),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun setTextAndIconColor(
    syncStatus: WorkerStatus
): Color {
    return when (syncStatus) {
        WorkerStatus.FAILED -> {
            Primary10
        }

        else -> {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}