package com.latticeonfhir.android.ui.login

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.service.smsretreiver.startSmsRetriever
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
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
                        text = stringResource(id = R.string.login_helper_text),
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
                            if (isInternetAvailable(activity)) {
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
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = activity.getString(R.string.no_internet_error_msg)
                                    )
                                }
                            }
                        },
                        enabled = !viewModel.isInputInvalid && viewModel.inputValue.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("BUTTON")
                    ) {
                        if (!viewModel.isAuthenticating) Text(text = stringResource(id = R.string.send_me_otp))
                        if (viewModel.isAuthenticating) ButtonLoader()
                    }
                }
            }
        }
    )

}