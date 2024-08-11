package com.latticeonfhir.android.ui.landingscreen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.ui.common.Detail
import com.latticeonfhir.android.ui.common.Label
import com.latticeonfhir.android.ui.theme.Primary10
import com.latticeonfhir.android.ui.theme.SyncFailedColor
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {

    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.twoMinuteTimer > 0, viewModel.showOtpFields) {
        if (viewModel.showOtpFields) {
            while (viewModel.twoMinuteTimer > 0) {
                delay(1000)
                viewModel.twoMinuteTimer -= 1
            }
        }
    }

    LaunchedEffect(viewModel.fiveMinuteTimer > 0) {
        while (viewModel.fiveMinuteTimer > 0) {
            if (viewModel.fiveMinuteTimer - 1 == 0) {
                viewModel.otpAttemptsExpired = false
                viewModel.errorMsg = ""
            }
            delay(1000)
            viewModel.fiveMinuteTimer -= 1
        }
    }

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
        if (viewModel.showConfirmDeleteAccountDialog) {
            DeleteAccountDialog(
                viewModel = viewModel,
                deleteAccountBtnClick = {
                    checkNetwork(
                        viewModel,
                        navController,
                        activity,
                        coroutineScope,
                        snackbarHostState
                    )
                },
                resendOtpClicked = {
                    if (isInternetAvailable(activity)) {
                        viewModel.isResending = true
                        viewModel.otpEntered = ""
                        viewModel.isOtpIncorrect = false
                        viewModel.resendOTP { resent ->
                            if (resent) {
                                viewModel.twoMinuteTimer = 120
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = activity.getString(R.string.no_internet_error_msg)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DeleteAccountDialog(
    viewModel: LandingScreenViewModel,
    deleteAccountBtnClick: () -> Unit,
    resendOtpClicked: () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {},
        content = {
            Surface(
                modifier = Modifier.padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_account),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.delete_account_dialog_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (viewModel.deleteAccountError.isNotBlank() || (viewModel.otpAttemptsExpired && !viewModel.showOtpFields)) {
                        Text(
                            text = if (viewModel.otpAttemptsExpired) viewModel.errorMsg else viewModel.deleteAccountError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    AnimatedVisibility(
                        visible = viewModel.showOtpFields
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.enter_otp_helping_text),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = viewModel.userEmail.ifBlank { viewModel.userPhoneNo },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(0.75f)
                            ) {
                                BasicTextField(
                                    value = viewModel.otpEntered,
                                    onValueChange = { value ->
                                        if (value.trim()
                                                .isEmpty() || (value.matches(onlyNumbers) && value.length <= 6)
                                        ) {
                                            if (!viewModel.otpAttemptsExpired) viewModel.errorMsg =
                                                ""
                                            viewModel.isOtpIncorrect = false
                                            viewModel.otpEntered = value
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp, bottom = 10.dp),
                                    textStyle = MaterialTheme.typography.titleLarge.copy(
                                        letterSpacing = 20.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true,
                                    maxLines = 1
                                )
                                HorizontalDivider(
                                    thickness = 1.5.dp,
                                    color = if (viewModel.isOtpIncorrect) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                            if (viewModel.errorMsg.isNotBlank()) {
                                Text(
                                    text = viewModel.errorMsg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (viewModel.twoMinuteTimer > 0 && !viewModel.otpAttemptsExpired) {
                                Text(
                                    text = "${
                                        String.format(
                                            Locale.ROOT,
                                            "%02d", viewModel.twoMinuteTimer / 60
                                        )
                                    }:${
                                        String.format(
                                            Locale.ROOT,
                                            "%02d",
                                            viewModel.twoMinuteTimer % 60
                                        )
                                    }",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier
                                        .padding(bottom = 16.dp, top = 24.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (viewModel.isResending || viewModel.isVerifying) {
                            Button(
                                onClick = {  },
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color.Transparent
                                )
                            ) {
                                ButtonLoader(color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            if (viewModel.showOtpFields && viewModel.fiveMinuteTimer == 0) {
                                TextButton(
                                    onClick = {
                                        // resend otp
                                        viewModel.errorMsg = ""
                                        viewModel.otpEntered = ""
                                        viewModel.isOtpIncorrect = false
                                        resendOtpClicked()
                                    },
                                    enabled = viewModel.twoMinuteTimer == 0
                                ) {
                                    Text(
                                        stringResource(id = R.string.resend_otp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1F))
                            TextButton(
                                onClick = {
                                    viewModel.showConfirmDeleteAccountDialog = false
                                    viewModel.showOtpFields = false
                                    viewModel.twoMinuteTimer = 120
                                    viewModel.isOtpIncorrect = false
                                    if (!viewModel.otpAttemptsExpired) {
                                        viewModel.errorMsg = ""
                                    }
                                },
                                modifier = Modifier.testTag("NEGATIVE_BTN")
                            ) {
                                Text(
                                    stringResource(id = R.string.cancel)
                                )
                            }
                            TextButton(
                                onClick = {
                                    deleteAccountBtnClick()
                                },
                                enabled = (!viewModel.showOtpFields || viewModel.otpEntered.length == 6) && !viewModel.otpAttemptsExpired
                            ) {
                                Text(
                                    stringResource(
                                        id = if (viewModel.showOtpFields) R.string.delete
                                        else R.string.delete_account
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
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
private fun DeleteAccountCard(
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

private fun checkNetwork(
    viewModel: LandingScreenViewModel,
    navController: NavController,
    activity: Activity,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when (isInternetAvailable(activity)) {
        true -> {
            if (viewModel.showOtpFields) {
                // validate otp
                verifyClick(
                    navController,
                    viewModel
                )
            } else {
                viewModel.otpEntered = ""
                viewModel.sendDeleteAccountOtp {
                    viewModel.showOtpFields = it
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

private fun verifyClick(navController: NavController, viewModel: LandingScreenViewModel) {
    viewModel.isVerifying = true
    viewModel.validateOtp {
        if (it) {
            CoroutineScope(Dispatchers.Main).launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "logoutUser",
                    true
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "logoutReason",
                    viewModel.logoutReason
                )
                navController.navigate(Screen.PhoneEmailScreen.route)
            }
        }
    }
}