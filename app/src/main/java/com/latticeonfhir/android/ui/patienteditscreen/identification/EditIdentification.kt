package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomTextFieldWithLength
import com.latticeonfhir.android.ui.common.IdSelectionChip
import com.latticeonfhir.android.ui.patientregistration.step2.AbhaIdVisualTransformation
import com.latticeonfhir.android.utils.constants.IdentificationConstants
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIdentification(
    navController: NavController,
    viewModel: EditIdentificationViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>("patient_details")
            viewModel.isEditing =
                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    "isEditing"
                ) == true
            viewModel.patient?.run {
                identifier.forEach { identity ->

                    when (identity.identifierType) {
                        IdentificationConstants.ABHA_ID_TYPE -> {
                            viewModel.abhaId = identity.identifierNumber
                            viewModel.isAbhaSelected = viewModel.abhaId.isNotBlank()
                        }

                        IdentificationConstants.RATION_CARD_TYPE -> {
                            viewModel.rationCard = identity.identifierNumber
                            viewModel.isRationCardSelected = viewModel.rationCard.isNotBlank()
                        }

                        else -> {
                            Timber.d("Something wrong")
                        }

                    }
                }
                viewModel.isAbhaSelectedTemp = viewModel.isAbhaSelected
                viewModel.isRationCardSelectedTemp = viewModel.isRationCardSelected
                viewModel.abhaIdTemp = viewModel.abhaId
                viewModel.rationCardTemp = viewModel.rationCard
            }
            viewModel.isLaunched = true

        }
    }

    BackHandler(enabled = true) {
        navController.previousBackStackEntry?.savedStateHandle?.set("isProfileUpdated", false)
        navController.popBackStack()
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Identification",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            false
                        )
                        navController.popBackStack()

                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "clear icon"
                        )
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    Text(
                        text = "Undo all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.checkIsEdit()) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(viewModel.checkIsEdit(), onClick = {
                                if (viewModel.revertChanges()) {
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar("Changes undone")

                                    }
                                }
                            })

                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IdSelectionChip(
                            idSelected = viewModel.isAbhaSelected,
                            label = stringResource(id = R.string.abha_id)
                        ) {
                            viewModel.isAbhaSelected = !it
                            if (!viewModel.isAbhaSelected) {
                                viewModel.abhaId = ""
                                viewModel.isAbhaIdValid = false
                            }
                        }
                        IdSelectionChip(
                            idSelected = viewModel.isRationCardSelected,
                            label = stringResource(id = R.string.ration_card)
                        ) {
                            viewModel.isRationCardSelected = !it
                            if (!viewModel.isRationCardSelected) {
                                viewModel.rationCard = ""
                                viewModel.isRationCardValid = false
                            }
                        }
                    }
                    AbhaIdComposable(viewModel)
                    RationCardComposable(viewModel)
                }
            }

        }, floatingActionButton = {
            Button(
                onClick = {
                    if (viewModel.abhaId.isNotEmpty() && viewModel.isAbhaSelected) {
                        viewModel.identifierList.add(
                            PatientIdentifier(
                                identifierType = IdentificationConstants.ABHA_ID_TYPE,
                                identifierNumber = viewModel.abhaId,
                                code = null
                            )
                        )
                    }
                    if (viewModel.rationCard.isNotEmpty() && viewModel.isRationCardSelected) {
                        viewModel.identifierList.add(
                            PatientIdentifier(
                                identifierType = IdentificationConstants.RATION_CARD_TYPE,
                                identifierNumber = viewModel.rationCard,
                                code = null
                            )
                        )
                    }
                    viewModel.updateBasicInfo(viewModel.patient!!.copy(identifier = viewModel.identifierList))
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "isProfileUpdated",
                        true
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 30.dp),
                enabled = viewModel.identityInfoValidation() && viewModel.checkIsEdit()
            ) {
                Text(text = "Save")
            }

        }
    )
}


@Composable
private fun AbhaIdComposable(viewModel: EditIdentificationViewModel) {
    if (viewModel.isAbhaSelected) {
        OutlinedTextField(
            value = viewModel.abhaId,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(14)

                viewModel.abhaId = digits
                viewModel.isAbhaIdValid = digits.length != 14
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = stringResource(R.string.abha_id))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = AbhaIdVisualTransformation(),
            isError = viewModel.isAbhaIdValid,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.isAbhaIdValid) {
                        Text(stringResource(R.string.abha_id_error_msg))
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${viewModel.abhaId.length}/${viewModel.maxAbhaIdLength}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun RationCardComposable(viewModel: EditIdentificationViewModel) {
    if (viewModel.isRationCardSelected) {
        CustomTextFieldWithLength(
            value = viewModel.rationCard,
            label = stringResource(id = R.string.ration_card),
            weight = 1f,
            maxLength = viewModel.maxRationCardLength,
            isError = viewModel.isRationCardValid,
            error = stringResource(id = R.string.ration_card_error_msg),
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Characters
        ) { input ->
            val value = input.uppercase()
                .filter { it.isLetterOrDigit() }
                .take(viewModel.maxRationCardLength)

            viewModel.rationCard = value
            viewModel.isRationCardValid = !viewModel.rationCardRegex.matches(value)
        }
    }
}