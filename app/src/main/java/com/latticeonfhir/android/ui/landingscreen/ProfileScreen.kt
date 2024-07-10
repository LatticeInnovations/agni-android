package com.latticeonfhir.android.ui.landingscreen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.Detail
import com.latticeonfhir.android.ui.common.Label
import com.latticeonfhir.android.ui.theme.Primary10
import com.latticeonfhir.android.ui.theme.SyncFailedColor
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {

    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
        AppVersionInfoCard()
        DeleteAccountCard(activity, viewModel, snackbarHostState, coroutineScope)
        AnimatedVisibility(viewModel.showConfirmDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.showConfirmDeleteAccountDialog = false
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.delete_account),
                        modifier = Modifier.testTag("DIALOG_TITLE")
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.delete_account_dialog_description),
                        modifier = Modifier.testTag("DIALOG_DESCRIPTION")
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showConfirmDeleteAccountDialog = false
                            checkNetwork(
                                viewModel,
                                navController,
                                activity,
                                coroutineScope,
                                snackbarHostState
                            )
                        },
                        modifier = Modifier.testTag("POSITIVE_BTN")
                    ) {
                        Text(
                            stringResource(id = R.string.delete_account)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.showConfirmDeleteAccountDialog = false
                        },
                        modifier = Modifier.testTag("NEGATIVE_BTN")
                    ) {
                        Text(
                            stringResource(id = R.string.cancel)
                        )
                    }
                }
            )
        }
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
            Text(
                text = stringResource(R.string.sync_status),
                style = MaterialTheme.typography.titleLarge
            )
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

@Composable
private fun AppVersionInfoCard() {
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
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.app_version),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.agni_app_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeleteAccountCard(
    activity: Activity,
    viewModel: LandingScreenViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clickable {
                if (isInternetAvailable(activity)) {
                    viewModel.showConfirmDeleteAccountDialog = true
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.no_internet_error_msg)
                        )
                    }
                }
            },
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.delete_account),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun checkNetwork(
    viewModel: LandingScreenViewModel,
    navController: NavController,
    activity: Activity,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when (isInternetAvailable(activity)) {
        true -> {
            viewModel.sendDeleteAccountOtp {
                if (it) {
                    CoroutineScope(Dispatchers.Main).launch {
                        navigate(viewModel, navController)
                    }
                }
            }
        }

        false -> {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = activity.getString(R.string.no_internet_error_msg)
                )
            }
        }
    }
}

fun navigate(viewModel: LandingScreenViewModel, navController: NavController) {
    navController.currentBackStackEntry?.savedStateHandle?.set(
        "userInput",
        viewModel.userEmail.ifBlank { viewModel.userPhoneNo }
    )
    navController.currentBackStackEntry?.savedStateHandle?.set(
        "isDeleteAccount",
        true
    )
    navController.navigate(Screen.OtpScreen.route)
}