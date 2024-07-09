package com.latticeonfhir.android.ui.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                        message = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                            "logoutReason"
                        )!!
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
                    InputField(viewModel)
                    Spacer(modifier = Modifier.height(45.dp))
                    Button(
                        onClick = {
                            when (isInternetAvailable(activity)) {
                                true -> proceed(viewModel, navController)
                                false -> {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = activity.getString(R.string.no_internet_error_msg)
                                        )
                                    }
                                }
                            }
                        },
                        enabled = viewModel.isEnabled(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("BUTTON")
                    ) {
                        if (!viewModel.isAuthenticating) Text(text = stringResource(id = R.string.send_me_otp))
                        else ButtonLoader()
                    }
                    if (viewModel.signUpButtonIsVisible) {
                        TextButton(onClick = { navController.navigate(Screen.SignUpPhoneEmailScreen.route) },
                            modifier =  Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 30.dp)) {
                            Text(
                                text = "Sign-up",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                AnimatedVisibility (viewModel.showDifferentUserLoginDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.showDifferentUserLoginDialog = false
                        },
                        title = {
                            Text(
                                text = stringResource(id = R.string.different_user_login_dialog_title),
                                modifier = Modifier.testTag("DIALOG_TITLE")
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.different_user_login_dialog_description),
                                modifier = Modifier.testTag("DIALOG_DESCRIPTION")
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.showDifferentUserLoginDialog = false
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
                                    stringResource(id = R.string.yes_proceed)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    viewModel.showDifferentUserLoginDialog = false
                                },
                                modifier = Modifier.testTag("NEGATIVE_BTN")
                            ) {
                                Text(
                                    stringResource(id = R.string.no_go_back)
                                )
                            }
                        }
                    )
                }
            }
        }
    )

}

fun checkNetwork(viewModel: PhoneEmailViewModel, navController: NavController, activity: Activity, coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState) {
    when (isInternetAvailable(activity)) {
        true -> {
            viewModel.clearAllAppData()
            navigate(viewModel, navController)
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

fun proceed(viewModel: PhoneEmailViewModel, navController: NavController) {
    if (viewModel.isDifferentUserLogin()) {
        viewModel.showDifferentUserLoginDialog = true
    } else {
        navigate(viewModel, navController)
    }
}

@Composable
fun InputField(viewModel: PhoneEmailViewModel) {
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
}

fun navigate(viewModel: PhoneEmailViewModel, navController: NavController) {
    viewModel.isAuthenticating = true
    viewModel.login {
        if (it) {
            CoroutineScope(Dispatchers.Main).launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "userInput",
                    viewModel.inputValue
                )
                navController.navigate(Screen.OtpScreen.route)
            }
        }
    }
}
