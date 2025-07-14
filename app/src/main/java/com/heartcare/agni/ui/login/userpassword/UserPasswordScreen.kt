package com.heartcare.agni.ui.login.userpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartcare.agni.R
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.utils.regex.RegexPatterns.atLeastOneAlphaAndNumber
import com.heartcare.agni.utils.regex.RegexPatterns.passwordRegex

@Composable
fun UserPasswordScreen(
    viewModel: UserPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(15.dp),
            ) {
                Spacer(Modifier.height(60.dp))
                Text(
                    text = stringResource(id = R.string.enter_user_id_and_password),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(50.dp))
                CustomTextField(
                    value = viewModel.userId,
                    label = stringResource(R.string.user_id),
                    weight = 1f,
                    maxLength = viewModel.maxUserIdLength,
                    isError = viewModel.isUserIdError,
                    error = viewModel.userIdError,
                    keyboardType = KeyboardType.Text,
                    keyboardCapitalization = KeyboardCapitalization.None,
                    updateValue = {
                        viewModel.userId = it
                        viewModel.isUserIdError =
                            viewModel.userId.length !in viewModel.minUserIdLength..viewModel.maxUserIdLength
                                    || !viewModel.userId.matches(atLeastOneAlphaAndNumber)
                        viewModel.userIdError =
                            if (viewModel.userId.isBlank()) context.getString(R.string.user_id_required_error_msg)
                            else context.getString(R.string.enter_valid_user_id)
                    }
                )
                Spacer(Modifier.height(16.dp))
                CustomTextField(
                    value = viewModel.password,
                    label = stringResource(R.string.password),
                    weight = 1f,
                    maxLength = viewModel.maxPasswordLength,
                    isError = viewModel.isPasswordError,
                    error = viewModel.passwordError,
                    keyboardType = KeyboardType.Password,
                    keyboardCapitalization = KeyboardCapitalization.None,
                    updateValue = {
                        viewModel.password = it
                        viewModel.isPasswordError = !viewModel.password.matches(passwordRegex)
                        viewModel.passwordError =
                            if (viewModel.password.isBlank()) context.getString(R.string.password_required_error_msg)
                            else context.getString(R.string.password_validation_error_msg)
                    },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        // forgot password
                    }
                ) {
                    Text(stringResource(R.string.forgot_password))
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        // continue button handle
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.continue_text))
                }
            }
        }
    )
}