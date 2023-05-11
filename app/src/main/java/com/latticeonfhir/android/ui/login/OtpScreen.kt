package com.latticeonfhir.android.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController, viewModel: OtpViewModel = viewModel()) {
    LaunchedEffect(viewModel.remainingTime > 0) {
        while (viewModel.remainingTime > 0) {
            delay(1000)
            viewModel.remainingTime -= 1
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
                            .padding(horizontal = 40.dp)
                    ) {
                        TextField(
                            value = viewModel.firstDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.firstDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        TextField(
                            value = viewModel.secondDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.secondDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        TextField(
                            value = viewModel.thirdDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.thirdDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        TextField(
                            value = viewModel.fourDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fourDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        TextField(
                            value = viewModel.fiveDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.fiveDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        TextField(
                            value = viewModel.sixDigit,
                            onValueChange = {
                                if (it.length <= 1 && it.matches(OnlyNumberRegex.onlyNumbers)) viewModel.sixDigit =
                                    it
                                viewModel.updateOtp()
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            )
                        )
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
                    if (viewModel.remainingTime > 0) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "${
                                String.format(
                                    "%02d", viewModel.remainingTime / 60
                                )
                            }:${
                                String.format(
                                    "%02d",
                                    viewModel.remainingTime % 60
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
                            onClick = { viewModel.remainingTime = 120 }
                        ) {
                            Text(
                                text = "Resend OTP",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = {
                            // call function of otp auth
                            // if authenticated, navigate to landing screen
                            viewModel.isVerifying = true
                        },
                        enabled = viewModel.isOtpValid,
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