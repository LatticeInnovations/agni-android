package com.latticeonfhir.android.ui.signup

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.ui.login.PhoneEmailViewModel
import com.latticeonfhir.android.ui.login.checkNetwork
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = true) {
        activity.finishAffinity()
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
                        text = stringResource(id = R.string.signup_helper_text),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    InputNameField(activity,viewModel)
                    Spacer(modifier = Modifier.height(30.dp))
                    InputClinicNameField(activity,viewModel)
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
                        Text(text = stringResource(id = R.string.submit))
                    }
                }
            }
        }
    )
}

fun proceed(viewModel: SignUpViewModel, navController: NavController) {
    navigate(viewModel, navController)
}

@Composable
fun InputNameField(activity: Activity, viewModel: SignUpViewModel) {
    OutlinedTextField(
        value = viewModel.inputName,
        onValueChange = {
            if (it.length <= 50) {
                viewModel.inputName = it.trim()
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
        placeholder = { Text(activity.getString(R.string.enter_your_name)) }
    )
}

@Composable
fun InputClinicNameField(activity: Activity, viewModel: SignUpViewModel) {
    OutlinedTextField(
        value = viewModel.inputClinicName,
        onValueChange = {
            if (it.length <= 50) {
                viewModel.inputClinicName = it.trim()
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
        placeholder = { Text(activity.getString(R.string.enter_your_clinic_name)) }
    )
}

fun navigate(viewModel: SignUpViewModel, navController: NavController) {
//    viewModel.isAuthenticating = true
//    viewModel.login {
//        if (it) {
//            CoroutineScope(Dispatchers.Main).launch {
//                navController.currentBackStackEntry?.savedStateHandle?.set(
//                    "userInput",
//                    viewModel.inputValue
//                )
//                navController.navigate(Screen.OtpScreen.route)
//            }
//        }
//    }
}
