package com.latticeonfhir.android.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController, viewModel: OtpViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel.twoMinuteTimer > 0) {
        while (viewModel.twoMinuteTimer > 0) {
            delay(1000)
            viewModel.twoMinuteTimer -= 1
        }
    }
    LaunchedEffect(viewModel.fiveMinuteTimer >= 0) {
        Timber.d("manseeyy ${viewModel.fiveMinuteTimer}")
        if (viewModel.fiveMinuteTimer > 0) {
            delay(1000)
            viewModel.fiveMinuteTimer -= 1
        } else if (viewModel.fiveMinuteTimer == 0){
            viewModel.otpAttemptsExpired = false
            viewModel.fiveMinuteTimer = 300
        }
    }
    LaunchedEffect(viewModel.isLaunched) {
        viewModel.userInput =
            navController.previousBackStackEntry?.savedStateHandle?.get<String>("userInput").toString()
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = viewModel.userInput,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        OtpTextField(viewModel.firstDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.firstDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.firstDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.secondDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.secondDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.secondDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.thirdDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.thirdDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.thirdDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.fourDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.fourDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fourDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.fiveDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
                            if (it.isEmpty()) viewModel.fiveDigit = it
                            else if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fiveDigit =
                                it
                            viewModel.updateOtp()
                        }
                        OtpTextField(viewModel.sixDigit, Modifier.weight(1f).padding(5.dp), viewModel.isOtpIncorrect){
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
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    if (viewModel.otpAttemptsExpired) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = viewModel.errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                    else{
                        if (viewModel.twoMinuteTimer > 0) {
                            Spacer(modifier = Modifier.height(15.dp))
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
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        } else {
                            TextButton(
                                onClick = {
                                    viewModel.firstDigit = ""
                                    viewModel.secondDigit = ""
                                    viewModel.thirdDigit = ""
                                    viewModel.fourDigit = ""
                                    viewModel.fiveDigit = ""
                                    viewModel.sixDigit = ""
                                    viewModel.isOtpIncorrect = false
                                    viewModel.updateOtp()
                                    viewModel.resendOTP {
                                        if (it)
                                            viewModel.twoMinuteTimer = 120
                                    }
                                }
                            ) {
                                Text(
                                    text = "Resend OTP",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
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
                            viewModel.isVerifying = true
                            viewModel.validateOtp {
                                if (it){
                                    CoroutineScope(Dispatchers.Main).launch {
                                        withContext(Dispatchers.Main){
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "LoggedIn",
                                                true
                                            )
                                            navController.navigate(Screen.LandingScreen.route){
                                                popUpTo(Screen.PhoneEmailScreen.route){
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = viewModel.isOtpValid && !viewModel.otpAttemptsExpired,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (viewModel.isVerifying) ButtonLoader()
                        else Text(text = "Verify")
                    }
                }
            }
        }
    )
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
        textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center)
    )
}