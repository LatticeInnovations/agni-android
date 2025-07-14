package com.heartcare.agni.ui.login.createpassword

import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.utils.constants.NavControllerConstants.PASSWORD_SCREEN
import com.heartcare.agni.utils.regex.RegexPatterns.passwordRegex

/***
 * 0 - Create new password
 * 1 - Reset password
 */
@Composable
fun CreatePasswordScreen(
    navController: NavController,
    viewModel: CreatePasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.screenFlag =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>(PASSWORD_SCREEN) ?: 0
            viewModel.isLaunched = true
        }
    }
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
                    text = when (viewModel.screenFlag) {
                        1 -> stringResource(id = R.string.reset_password)
                        else -> stringResource(id = R.string.create_new_password)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(50.dp))
                NewPasswordField(viewModel, context)
                Spacer(Modifier.height(16.dp))
                ConfirmPasswordField(viewModel, context)
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        // save password
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.validation()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    )
}

@Composable
private fun NewPasswordField(
    viewModel: CreatePasswordViewModel,
    context: Context
) {
    CustomTextField(
        value = viewModel.newPassword,
        label = stringResource(R.string.new_password),
        weight = 1f,
        maxLength = viewModel.maxPasswordLength,
        isError = viewModel.isNewPasswordError,
        error = stringResource(R.string.password_validation_error_msg),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.None,
        updateValue = {
            viewModel.newPassword = it
            viewModel.isNewPasswordError = !viewModel.newPassword.matches(passwordRegex)
            if (viewModel.hasInteractedWithConfirmPassword) {
                updateConfirmPasswordError(viewModel, context)
            }
        },
        trailingIcon = if (viewModel.isNewPasswordVisible) painterResource(R.drawable.visibility)
        else painterResource(R.drawable.visibility_off),
        trailingIconClick = {
            viewModel.isNewPasswordVisible = !viewModel.isNewPasswordVisible
        },
        visualTransformation = if (viewModel.isNewPasswordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation()
    )
}

@Composable
private fun ConfirmPasswordField(
    viewModel: CreatePasswordViewModel,
    context: Context
) {
    CustomTextField(
        value = viewModel.confirmPassword,
        label = stringResource(R.string.confirm_password),
        weight = 1f,
        maxLength = viewModel.maxPasswordLength,
        isError = viewModel.isConfirmPasswordError,
        error = viewModel.confirmPasswordError,
        keyboardType = KeyboardType.Password,
        keyboardCapitalization = KeyboardCapitalization.None,
        updateValue = {
            viewModel.confirmPassword = it
            viewModel.hasInteractedWithConfirmPassword = true
            updateConfirmPasswordError(viewModel, context)
        },
        trailingIcon = if (viewModel.isConfirmPasswordVisible) painterResource(R.drawable.visibility)
        else painterResource(R.drawable.visibility_off),
        trailingIconClick = {
            viewModel.isConfirmPasswordVisible = !viewModel.isConfirmPasswordVisible
        },
        visualTransformation = if (viewModel.isConfirmPasswordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation()
    )
}

fun updateConfirmPasswordError(
    viewModel: CreatePasswordViewModel,
    context: Context
) {
    viewModel.confirmPasswordError =
        if (viewModel.confirmPassword != viewModel.newPassword) context.getString(
            R.string.passwords_do_not_match
        )
        else if (!viewModel.confirmPassword.matches(passwordRegex))
            context.getString(R.string.password_validation_error_msg)
        else ""
    viewModel.isConfirmPasswordError = viewModel.confirmPasswordError.isNotBlank()
}
