package com.latticeonfhir.android.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.ui.main.MainActivity
import com.latticeonfhir.android.utils.network.CheckNetwork
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController, viewModel: OtpViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as MainActivity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.userInput =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("userInput")
                    .toString()
            viewModel.isSignUp = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isSignUp") != null
            viewModel.isDeleteAccount = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isDeleteAccount") != null
            if (viewModel.userInput.matches(onlyNumbers)) {
                activity.registerBroadcastReceiver()
            }
            viewModel.focusRequesters[0].requestFocus()
            viewModel.isLaunched = true
        }
    }
    LaunchedEffect(activity.otp) {
        if (activity.otp.isNotEmpty()) {
            activity.otp.trim().forEachIndexed { index, c ->
                viewModel.otpValues[index].value = c.toString()
            }
            viewModel.updateOtp()
            verifyClick(navController, viewModel)
            activity.otp = ""
            activity.unregisterBroadcastReceiver()
        }
    }
    LaunchedEffect(viewModel.twoMinuteTimer > 0) {
        while (viewModel.twoMinuteTimer > 0) {
            delay(1000)
            viewModel.twoMinuteTimer -= 1
        }
    }
    LaunchedEffect(viewModel.fiveMinuteTimer > 0) {
        while (viewModel.fiveMinuteTimer > 0) {
            if (viewModel.fiveMinuteTimer - 1 == 0) {
                viewModel.otpAttemptsExpired = false
                viewModel.fiveMinuteTimer = 300
            }
            delay(1000)
            viewModel.fiveMinuteTimer -= 1
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {

                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.enter_otp_helping_text),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                    Text(
                        text = viewModel.userInput,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("SUB_HEADING_TAG")
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(6) { index ->
                            OtpTextField(
                                index = index,
                                value = viewModel.otpValues[index].value,
                                modifier = Modifier
                                    .width(45.dp)
                                    .focusRequester(viewModel.focusRequesters[index])
                                    .testTag("m_pin_Field")
                                    .onKeyEvent { keyEvent ->
                                        setKeyEvent(keyEvent, viewModel, index)
                                    },
                                errorCondition = viewModel.isOtpIncorrect
                            ) { value ->
                                if (value.isNotBlank()) {
                                    if (index < 5) {
                                        viewModel.focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                    viewModel.otpValues[index].value = value.trim()
                                    viewModel.updateOtp()
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                    }
                    if (viewModel.isOtpIncorrect) {
                        Text(
                            text = viewModel.errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("ERROR_MSG"),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    if (viewModel.otpAttemptsExpired) {
                        Text(
                            text = viewModel.errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp)
                                .testTag("ERROR_MSG"),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        // Two Minute Timer
                        TwoMinuteTimer(
                            viewModel,
                            coroutineScope,
                            activity,
                            snackbarHostState
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = {
                            // call function of otp auth
                            // if authenticated, navigate to landing screen
                            if (CheckNetwork.isInternetAvailable(activity)) {
                                verifyClick(navController, viewModel)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.no_internet_error_msg)
                                    )
                                }
                            }
                        },
                        enabled = viewModel.otpEntered.length == 6 && !viewModel.otpAttemptsExpired,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("BUTTON")
                    ) {
                        if (viewModel.isVerifying) ButtonLoader()
                        else {
                            if(viewModel.isDeleteAccount) Text(text = stringResource(id = R.string.delete_account)) else Text(text = stringResource(id = R.string.verify))
                        }
                    }
                }
            }
        }
    )
}

private fun setKeyEvent(keyEvent: KeyEvent, viewModel: OtpViewModel, index: Int): Boolean {
    return if (keyEvent.key == Key.Backspace) {
        if (index > 0) {
            if (viewModel.otpValues[index].value.isBlank()) {
                viewModel.focusRequesters[index - 1].requestFocus()
                viewModel.otpValues[index - 1].value = ""
            } else viewModel.otpValues[index].value = ""
        } else {
            viewModel.otpValues[index].value = ""
        }
        viewModel.updateOtp()
        true
    } else {
        false
    }
}

@Composable
fun TwoMinuteTimer(
    viewModel: OtpViewModel,
    coroutineScope: CoroutineScope,
    activity: MainActivity,
    snackbarHostState: SnackbarHostState
) {
    if (viewModel.twoMinuteTimer > 0) {
        Text(
            text = "${
                String.format(
                    "%02d", viewModel.twoMinuteTimer / 60
                )
            }:${
                String.format(
                    "%02d",
                    viewModel.twoMinuteTimer % 60
                )
            }",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .testTag("TWO_MIN_TIMER"),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        ResendButton(
            viewModel,
            coroutineScope,
            activity,
            snackbarHostState
        )
    }
}

@Composable
fun ResendButton(
    viewModel: OtpViewModel,
    coroutineScope: CoroutineScope,
    activity: MainActivity,
    snackbarHostState: SnackbarHostState
) {
    if (viewModel.isResending) {
        Spacer(modifier = Modifier.height(15.dp))
        ButtonLoader()
        Spacer(modifier = Modifier.height(15.dp))
    } else TextButton(
        onClick = {
            if (CheckNetwork.isInternetAvailable(activity)) {
                viewModel.isResending = true
                viewModel.otpValues.forEach { it.value = "" }
                viewModel.isOtpIncorrect = false
                viewModel.updateOtp()
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
    ) {
        Text(
            text = stringResource(id = R.string.resend_otp),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("RESEND_BUTTON"),
            textAlign = TextAlign.Center
        )
    }
}

fun verifyClick(navController: NavController, viewModel: OtpViewModel) {
    viewModel.isVerifying = true
    viewModel.validateOtp {
        if (it) {
            CoroutineScope(Dispatchers.Main).launch {
                if(viewModel.isSignUp) {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "userInput",
                        viewModel.userInput
                    )
                    navController.navigate(Screen.SignUpScreen.route)
                } else if(viewModel.isDeleteAccount) {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "logoutUser",
                        true
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "logoutReason",
                        "Thank You, your account will be deleted and confirmation will be given within 48 hours."
                    )
                    navController.navigate(Screen.PhoneEmailScreen.route)
                } else {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "loggedIn",
                        true
                    )
                    navController.navigate(Screen.LandingScreen.route)
                }
            }
        }
    }
}

@Composable
fun OtpTextField(
    index: Int,
    value: String,
    modifier: Modifier,
    errorCondition: Boolean,
    updateValue: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.length <= 1 && it.matches(onlyNumbers))
                updateValue(it)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            errorTextColor = MaterialTheme.colorScheme.error,
        ),
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = if (index == 5) ImeAction.Done else ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        isError = errorCondition,
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
    )
}