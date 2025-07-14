package com.heartcare.agni.ui.login.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.ui.common.ButtonLoader
import com.heartcare.agni.utils.constants.NavControllerConstants.PIN_SCREEN

/***
 * 0 - Login with m-pin
 * 1 - create m-pin
 * 2 - reset m-pin
 */
@Composable
fun PinScreen(
    navController: NavController,
    viewModel: PinViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.screenFlag =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>(PIN_SCREEN) ?: 0
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        topBar = {
            AppBarComposable(viewModel, navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                HeaderTexts(viewModel)

                Spacer(modifier = Modifier.height(60.dp))

                PinFields(viewModel)

                if (viewModel.screenFlag == 0) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = {
                                // forgot m-pin
                            }
                        ) {
                            Text(stringResource(R.string.forgot_pin))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        // save pin
                        viewModel.isLoading = true
                    },
                    enabled = viewModel.pinValues.joinToString("") { it.value }.length == viewModel.pinLength,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (viewModel.isLoading) ButtonLoader()
                    else Text(
                        when (viewModel.screenFlag) {
                            1, 2 -> stringResource(R.string.save_pin)
                            else -> stringResource(R.string.login)
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBarComposable(
    viewModel: PinViewModel,
    navController: NavController
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            if (viewModel.screenFlag != 0) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
private fun HeaderTexts(viewModel: PinViewModel) {
    Column {
        Text(
            text = when (viewModel.screenFlag) {
                1 -> stringResource(R.string.create_pin)
                2 -> stringResource(R.string.reset_pin)
                else -> stringResource(R.string.enter_pin)
            },
            style = MaterialTheme.typography.headlineSmall
        )
        if (viewModel.screenFlag == 1) {
            Text(
                text = stringResource(R.string.pin_info),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PinFields(viewModel: PinViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {

            repeat(4) { index ->
                MPinTextField(
                    value = viewModel.pinValues[index].value,
                    modifier = Modifier
                        .width(53.dp)
                        .height(56.dp)
                        .focusRequester(viewModel.focusRequesters[index])
                        .onKeyEvent { keyEvent ->
                            setKeyEvent(
                                keyEvent,
                                viewModel,
                                index
                            )
                        },
                    errorCondition = viewModel.pinError,
                    maxLength = viewModel.pinLength,
                    next = {
                        if (index < 3) {
                            viewModel.focusRequesters[index + 1].requestFocus()
                        }
                    }
                ) { value ->
                    if (viewModel.pinValues[index].value.isBlank()) {
                        viewModel.pinValues[index].value = value.trim().filter {
                            it.isDigit()
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun MPinTextField(
    value: String,
    modifier: Modifier,
    errorCondition: Boolean,
    maxLength: Int,
    callBack: (() -> Unit)? = null,
    next: (() -> Unit)? = null,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        shape = RoundedCornerShape(4.dp),
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                updateValue(it)
                if (it.isNotEmpty()) next?.invoke()
            } else
                next?.invoke()
        },
        modifier = modifier,
        maxLines = maxLength,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(onSend = {
            callBack?.invoke()
        }),
        isError = errorCondition,
        textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            textGeometricTransform = TextGeometricTransform()
        )
    )
}


private fun setKeyEvent(keyEvent: KeyEvent, viewModel: PinViewModel, index: Int): Boolean {
    return if (keyEvent.key == Key.Backspace) {
        if (index > 0) {
            viewModel.pinValues[index].value = ""
            viewModel.focusRequesters[index - 1].requestFocus()
        } else {
            viewModel.pinValues[index].value = ""
        }
        true
    } else {
        false
    }
}