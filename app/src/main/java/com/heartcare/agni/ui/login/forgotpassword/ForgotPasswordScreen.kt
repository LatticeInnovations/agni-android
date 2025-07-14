package com.heartcare.agni.ui.login.forgotpassword

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartcare.agni.R

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel()
) {
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
                    text = stringResource(id = R.string.enter_email_number),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(50.dp))
                OutlinedTextField(
                    value = viewModel.inputValue,
                    onValueChange = {
                        if (it.length <= 50) {
                            viewModel.inputValue = it.trim()
                            viewModel.updateError()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text(text = if (viewModel.isError) stringResource(R.string.enter_valid_phone_number_or_email) else "")
                    },
                    isError = viewModel.isError,
                    singleLine = true,
                    leadingIcon = if (viewModel.isPhoneNumber) {
                        {
                            Text(text = " +91", color = MaterialTheme.colorScheme.outline)
                        }
                    } else null
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        // Continue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isError
                ) {
                    Text(stringResource(R.string.continue_text))
                }
            }
        }
    )
}