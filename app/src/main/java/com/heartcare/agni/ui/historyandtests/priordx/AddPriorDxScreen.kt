package com.heartcare.agni.ui.historyandtests.priordx

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.PriorDiagnosis
import com.heartcare.agni.data.local.enums.PriorDiagnosis.Companion.getListOfPriorDx
import com.heartcare.agni.ui.common.CheckBoxRow
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.PRIOR_DX_SAVED

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPriorDxScreen(
    navController: NavController,
    viewModel: AddPriorDxViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.add_prior_dx),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.Clear,  contentDescription = "CLEAR_ICON")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    text = stringResource(R.string.prior_dx_header_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
                PriorDxListComposable(viewModel)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {
                Button(
                    onClick = {
                        // save prior dx
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            PRIOR_DX_SAVED,
                            true
                        )
                        navController.navigateUp()
                    },
                    enabled = viewModel.isValid(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    )
}

@Composable
private fun PriorDxListComposable(
    viewModel: AddPriorDxViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        getListOfPriorDx().forEach { dx ->
            val isChecked = dx in viewModel.selectedPriorDx

            CheckBoxRow(
                isChecked = isChecked,
                onCheckedChange = { checked ->
                    if (checked) {
                        viewModel.selectedPriorDx += dx
                    } else {
                        viewModel.selectedPriorDx -= dx
                        when (dx) {
                            PriorDiagnosis.CANCER.display -> {
                                viewModel.cancerField = ""
                                viewModel.isCancerFieldError = false
                            }
                            PriorDiagnosis.OTHERS.display -> {
                                viewModel.otherField = ""
                                viewModel.isOtherFieldError = false
                            }
                        }
                    }
                },
                label = dx
            )

            OtherField(
                isVisible = dx == PriorDiagnosis.CANCER.display && isChecked,
                value = viewModel.cancerField,
                isError = viewModel.isCancerFieldError,
                maxLength = viewModel.maxCancerFieldLength,
                onValueChange = {
                    viewModel.cancerField = it
                    viewModel.isCancerFieldError = it.isBlank()
                }
            )

            OtherField(
                isVisible = dx == PriorDiagnosis.OTHERS.display && isChecked,
                value = viewModel.otherField,
                isError = viewModel.isOtherFieldError,
                maxLength = viewModel.maxOtherFieldLength,
                onValueChange = {
                    viewModel.otherField = it
                    viewModel.isOtherFieldError = it.isBlank()
                }
            )
        }
    }
}

@Composable
private fun OtherField(
    isVisible: Boolean,
    value: String,
    isError: Boolean,
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        CustomTextFieldWithLength(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = value,
            placeholder = stringResource(R.string.please_specify_mandatory),
            weight = 1f,
            maxLength = maxLength,
            isError = isError,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            updateValue = onValueChange
        )
    }
}
