package com.latticeonfhir.android.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.network.CheckNetwork
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.ui.main.MainActivity
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController, viewModel: OtpViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val activity = LocalContext.current as MainActivity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    activity.registerBroadcastReceiver()
    LaunchedEffect(activity.otp){
        if (activity.otp.isNotEmpty()) {
            viewModel.firstDigit = activity.otp[0].toString()
            viewModel.secondDigit = activity.otp[1].toString()
            viewModel.thirdDigit = activity.otp[2].toString()
            viewModel.fourDigit = activity.otp[3].toString()
            viewModel.fiveDigit = activity.otp[4].toString()
            viewModel.sixDigit = activity.otp[5].toString()
            viewModel.updateOtp()
            verifyClick(navController, viewModel)
            activity.otp = ""
            activity.unregisterBroadcastReceiver()
        }
    }
    LaunchedEffect(viewModel.isLaunched) {
        viewModel.userInput =
            navController.previousBackStackEntry?.savedStateHandle?.get<String>("userInput").toString()
        viewModel.isLaunched = true
    }
    LaunchedEffect(
        key1 = viewModel.secondDigit,
    ) {
        if (viewModel.secondDigit.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        } else {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
        }
    }
    LaunchedEffect(
        key1 = viewModel.thirdDigit,
    ) {
        if (viewModel.thirdDigit.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        } else {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
        }
    }
    LaunchedEffect(
        key1 = viewModel.fourDigit,
    ) {
        if (viewModel.fourDigit.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        } else {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
        }
    }
    LaunchedEffect(
        key1 = viewModel.fiveDigit,
    ) {
        if (viewModel.fiveDigit.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        } else {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
        }
    }
    LaunchedEffect(
        key1 = viewModel.sixDigit,
    ) {
        if (viewModel.sixDigit.isEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
        }
    }
    LaunchedEffect(
        key1 = viewModel.firstDigit,
    ) {
        if (viewModel.firstDigit.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        } else {
            focusRequester.requestFocus()
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
           if (viewModel.fiveMinuteTimer - 1 == 0){
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
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
                        text = "Enter the OTP we sent to",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        OtpTextField(viewModel.firstDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .focusRequester(focusRequester)
                                .testTag("FIRST_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.firstDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.firstDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.secondDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .testTag("SECOND_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.secondDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.secondDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.thirdDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .testTag("THIRD_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.thirdDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.thirdDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.fourDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .testTag("FOUR_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.fourDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fourDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.fiveDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .testTag("FIVE_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.fiveDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fiveDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.sixDigit,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                                .testTag("SIX_DIGIT"), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.sixDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.sixDigit =
                                it
                            viewModel.updateOtp()
                        }
                    }
                    if (viewModel.isOtpIncorrect) {
                        Text(
                            text = viewModel.errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().testTag("ERROR_MSG"),
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
                                .padding(vertical = 15.dp).testTag("ERROR_MSG"),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else{
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
                            if (viewModel.isResending) {
                                Spacer(modifier = Modifier.height(15.dp))
                                ButtonLoader()
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            else TextButton(
                                onClick = {
                                    viewModel.isResending = true
                                    viewModel.firstDigit = ""
                                    viewModel.secondDigit = ""
                                    viewModel.thirdDigit = ""
                                    viewModel.fourDigit = ""
                                    viewModel.fiveDigit = ""
                                    viewModel.sixDigit = ""
                                    viewModel.isOtpIncorrect = false
                                    viewModel.updateOtp()
                                    viewModel.resendOTP { resent ->
                                        if (resent) {
                                            viewModel.twoMinuteTimer = 120
                                            viewModel.isResending = false
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = "Resend OTP",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth().testTag("RESEND_BUTTON"),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
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
                                        message = "Please connect to internet and try again"
                                    )
                                }
                            }
                        },
                        enabled = viewModel.otpEntered.length == 6 && !viewModel.otpAttemptsExpired,
                        modifier = Modifier.fillMaxWidth().testTag("BUTTON")
                    ) {
                        if (viewModel.isVerifying) ButtonLoader()
                        else Text(text = "Verify")
                    }
                }
            }
        }
    )
}

fun verifyClick(navController: NavController, viewModel: OtpViewModel) {
    viewModel.isVerifying = true
    viewModel.validateOtp {
        if (it){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main){
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
fun OtpTextField(value: String, modifier: Modifier, errorCondition: Boolean, updateValue: (String) -> Unit){
    TextField(
        value = value,
        onValueChange = {
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
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        isError = errorCondition,
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
    )
}