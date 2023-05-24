package com.latticeonfhir.android.ui.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.service.smsretreiver.startSmsRetriever
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PhoneEmailScreen(
    navController: NavController,
    viewModel: PhoneEmailViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = true) {
        activity.finishAffinity()
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("logoutUser") == true) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = navController.previousBackStackEntry?.savedStateHandle?.get<String>("logoutReason")!!
                    )
                }
            }
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),

                    ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        text = "Login with phone number or email address",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    OutlinedTextField(
                        value = viewModel.inputValue,
                        onValueChange = {
                            if (it.length <= 50) {
                                viewModel.inputValue = it.trim()
                                viewModel.updateError()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("INPUT_FIELD"),
                        supportingText = if (viewModel.isError) {
                            {
                                Text(text = viewModel.errorMsg)
                            }
                        } else null,
                        isError = viewModel.isError,
                        singleLine = true,
                        leadingIcon = if (viewModel.isPhoneNumber) {
                            {
                                Text(text = " +91", color = MaterialTheme.colorScheme.outline)
                            }
                        } else null
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    Button(
                        onClick = {
                            viewModel.isAuthenticating = true
                            viewModel.login {
                                if (it) {
                                    if (viewModel.isPhoneNumber) {
                                        startSmsRetriever(activity)
                                    }
                                    CoroutineScope(Dispatchers.Main).launch {
                                        withContext(Dispatchers.Main) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "userInput",
                                                viewModel.inputValue
                                            )
                                            navController.navigate(Screen.OtpScreen.route)
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !viewModel.isInputInvalid && viewModel.inputValue.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("BUTTON")
                    ) {
                        if (!viewModel.isAuthenticating) Text(text = "Send me OTP")
                        if (viewModel.isAuthenticating) ButtonLoader()
                    }
                }
            }
        }
    )

}