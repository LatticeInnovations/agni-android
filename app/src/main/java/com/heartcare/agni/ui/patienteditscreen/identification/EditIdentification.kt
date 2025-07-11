package com.heartcare.agni.ui.patienteditscreen.identification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.NationalIdUse
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.utils.constants.IdentificationConstants
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import com.heartcare.agni.utils.regex.RegexPatterns.atLeastOneAlphaAndNumber
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
                        IdentificationConstants.HOSPITAL_ID -> {
                            viewModel.hospitalId = identity.identifierNumber
                        }

                        IdentificationConstants.NATIONAL_ID -> {
                            viewModel.nationalId = identity.identifierNumber
                            viewModel.isNationalIdVerified =
                                identity.use == NationalIdUse.OFFICIAL.use
                            viewModel.nationalIdUse = identity.use!!
                        }

                        else -> {
                            Timber.d("Something wrong")
                        }
                    }
                }
                viewModel.isVerifyClicked = viewModel.nationalIdUse.isNotBlank()
                viewModel.hospitalIdTemp = viewModel.hospitalId
                viewModel.nationalIdTemp = viewModel.nationalId
                viewModel.nationalIdUseTemp = viewModel.nationalIdUse
                viewModel.isNationalIdVerifiedTemp = viewModel.isNationalIdVerified
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
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.identification),
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
                    TextButton(
                        onClick = {
                            if (viewModel.revertChanges()) {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("Changes undone")
                                }
                            }
                        },
                        enabled = viewModel.checkIsEdit()
                    ) {
                        Text(text = stringResource(R.string.undo_all))
                    }
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
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HospitalIdComposable(viewModel)
                    NationalIdComposable(viewModel)
                }
            }
        },
        floatingActionButton = {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        if (viewModel.hospitalId.isNotEmpty()) {
                            viewModel.identifierList.add(
                                PatientIdentifier(
                                    identifierType = IdentificationConstants.HOSPITAL_ID,
                                    identifierNumber = viewModel.hospitalId,
                                    code = null,
                                    use = null
                                )
                            )
                        }
                        if (viewModel.nationalId.isNotEmpty()) {
                            viewModel.identifierList.add(
                                PatientIdentifier(
                                    identifierType = IdentificationConstants.NATIONAL_ID,
                                    identifierNumber = viewModel.nationalId,
                                    use = if (viewModel.isNationalIdVerified) NationalIdUse.OFFICIAL.use
                                        else NationalIdUse.TEMP.use,
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
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    )
}

@Composable
private fun HospitalIdComposable(
    viewModel: EditIdentificationViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.hospitalId,
        label = stringResource(id = R.string.hospital_id),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxHospitalIdLength,
        isError = viewModel.isHospitalIdValid,
        error = stringResource(id = R.string.hospital_id_error_msg),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Characters
    ) { value ->
        val filtered = value.filter { it.isLetterOrDigit() }
        if (value.length <= viewModel.maxHospitalIdLength && (value == filtered || value.isEmpty()))
            viewModel.hospitalId = value
        viewModel.isHospitalIdValid = viewModel.hospitalId.isNotBlank() &&
                !atLeastOneAlphaAndNumber.matches(viewModel.hospitalId)
    }
}

@Composable
private fun NationalIdComposable(
    viewModel: EditIdentificationViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = viewModel.nationalId,
            onValueChange = {
                if (it.length <= viewModel.maxNationalIdLength && (it.matches(onlyNumbers) || it.isEmpty())) {
                    viewModel.nationalId = it
                    viewModel.isVerifyClicked = false
                    viewModel.isNationalIdVerified = false
                }
            },
            label = {
                Text(stringResource(R.string.national_id))
            },
            modifier = Modifier.weight(3f),
            supportingText = {
                NationalIdSupportingText(viewModel)
            },
            trailingIcon = {
                if (viewModel.nationalId.isNotBlank())
                    IconButton(
                        onClick = {
                            viewModel.nationalId = ""
                            viewModel.isVerifyClicked = false
                            viewModel.isNationalIdVerified = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cancel),
                            contentDescription = null
                        )
                    }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Button(
            onClick = {
                viewModel.isVerifyClicked = true
                // TODO : add logic to verify national id
                viewModel.isNationalIdVerified = false
            },
            modifier = Modifier.weight(1f),
            enabled = viewModel.nationalId.isNotBlank() && !viewModel.isNationalIdVerified
        ) {
            Text(stringResource(R.string.verify))
        }
    }
}

@Composable
private fun NationalIdSupportingText(
    viewModel: EditIdentificationViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.isVerifyClicked) {
            val text: String
            val icon: Int
            val color: Color
            if (viewModel.isNationalIdVerified) {
                text = stringResource(R.string.verified)
                icon = R.drawable.sync_completed_icon
                color = MaterialTheme.colorScheme.primary
            } else {
                text = stringResource(R.string.unverified)
                icon = R.drawable.info
                color = MaterialTheme.colorScheme.onSurfaceVariant
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${viewModel.nationalId.length}/${viewModel.maxNationalIdLength}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}