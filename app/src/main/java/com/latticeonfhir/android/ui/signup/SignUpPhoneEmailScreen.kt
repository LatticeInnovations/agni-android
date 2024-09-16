package com.latticeonfhir.android.ui.signup

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.network.CheckNetwork.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpPhoneEmailScreen(
    navController: NavController,
    viewModel: SignUpPhoneEmailViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                        text = stringResource(id = R.string.signup_helper_text_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    InputField(activity, viewModel)
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
                        if (!viewModel.isAuthenticating) Text(text = stringResource(id = R.string.submit))
                        else ButtonLoader()
                    }
                }
            }
        }
    )

}

fun proceed(viewModel: SignUpPhoneEmailViewModel, navController: NavController) {
    navigate(viewModel, navController)
}

@Composable
fun InputField(activity: Activity, viewModel: SignUpPhoneEmailViewModel) {
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
        } else null,
        placeholder = {
            Text(activity.getString(R.string.enter_phone_number_or_email))
        }
    )
}

fun navigate(viewModel: SignUpPhoneEmailViewModel, navController: NavController) {
    viewModel.isAuthenticating = true
    viewModel.signUp {
        if (it) {
            CoroutineScope(Dispatchers.Main).launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "userInput",
                    viewModel.inputValue
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "isSignUp",
                    true
                )
                navController.navigate(Screen.OtpScreen.route)
            }
        }
    }
}
