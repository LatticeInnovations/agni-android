package com.latticeonfhir.android.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.ButtonLoader
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex

@Composable
fun PhoneEmailScreen(navController: NavController, viewModel: PhoneEmailViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),

        ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Login with phone number or email address",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(50.dp))
        OutlinedTextField(
            value = viewModel.inputValue,
            onValueChange = {
                viewModel.isOnlyNumber = it.matches(OnlyNumberRegex.onlyNumbers)
                viewModel.inputValue = it
                viewModel.updateError()
            },
            modifier = Modifier.fillMaxWidth(),
            supportingText = if (viewModel.isError) {
                {
                    Text(text = viewModel.errorMsg)
                }
            } else null,
            isError = viewModel.isError,
            singleLine = true,
            leadingIcon = if (viewModel.isOnlyNumber) {
                {
                    Text(text = " +91", color = MaterialTheme.colorScheme.outline)
                }
            } else null
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                if (!viewModel.isAuthenticating) {
                    viewModel.isAuthenticating = true
                    // call function to check if the user exists in db or not
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "userInput",
                        viewModel.inputValue
                    )
                    navController.navigate(Screen.OtpScreen.route)
                }
            },
            enabled = !viewModel.isError && viewModel.inputValue.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!viewModel.isAuthenticating) Text(text = "Send me OTP")
            if (viewModel.isAuthenticating) ButtonLoader()
        }
    }
}