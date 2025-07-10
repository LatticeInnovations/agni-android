package com.heartcare.agni.ui.patientregistration.step3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun PatientRegistrationStepThree(
    navController: NavController,
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepThreeViewModel = hiltViewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            with(viewModel) {
                province = patientRegister.province
                areaCouncil = patientRegister.areaCouncil
                island = patientRegister.island
                village = patientRegister.village
                otherProvince = patientRegister.otherProvince.toString()
                otherAreaCouncil = patientRegister.otherAreaCouncil.toString()
                otherIsland = patientRegister.otherIsland.toString()
                otherVillage = patientRegister.otherVillage.toString()
                postalCode = patientRegister.postalCode.toString()
                isProvinceOtherSelected = otherProvince.isNotBlank()
                isAreaCouncilOtherSelected = otherAreaCouncil.isNotBlank()
                isIslandOtherSelected = otherIsland.isNotBlank()
                isVillageOtherSelected = otherVillage.isNotBlank()
            }
            viewModel.isLaunched = true
        }
    }

    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(patientRegistrationViewModel)

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            key(viewModel.provinceList, viewModel.areaCouncilList, viewModel.islandList, viewModel.villageList) {
                AddressHierarchy(viewModel)
            }
        }

        Button(
            onClick = {
                with(patientRegister) {
                    province = viewModel.province
                    areaCouncil = viewModel.areaCouncil
                    island = viewModel.island
                    village = viewModel.village
                    postalCode = viewModel.postalCode
                    otherProvince = viewModel.otherProvince
                    otherAreaCouncil = viewModel.otherAreaCouncil
                    otherIsland = viewModel.otherIsland
                    otherVillage = viewModel.otherVillage
                }

                navController.currentBackStackEntry?.savedStateHandle?.set("patient_register_details", patientRegister)

                navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            enabled = viewModel.addressInfoValidation()
        ) {
            Text(text = stringResource(id = R.string.submit_and_preview))
        }
    }
}

@Composable
private fun HeaderSection(viewModel: PatientRegistrationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.addresses),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Page 3/${viewModel.totalSteps}",
            style = MaterialTheme.typography.bodySmall,
            color = Neutral40
        )
    }
}

@Composable
private fun AddressHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    DropDownComposable(
        value = viewModel.province?.name ?: "",
        updateValue = {
            viewModel.province = it
            viewModel.isProvinceOtherSelected = it == viewModel.other
            if (!viewModel.isProvinceOtherSelected) {
                viewModel.otherProvince = ""
                resetAreaCouncilHierarchy(viewModel)
                viewModel.getAreaCouncilList()
            }
        },
        label = stringResource(id = R.string.province_mandatory),
        dropdownList = viewModel.provinceList,
        errorText = stringResource(R.string.province_required),
        isMandatory = true,
        isEnabled = true
    )

    if (viewModel.isProvinceOtherSelected) {
        OtherProvinceComposable(viewModel)
        OtherAreaCouncilComposable(viewModel)
        OtherIslandComposable(viewModel)
        OtherVillageComposable(viewModel)
    } else {
        DropDownComposable(
            value = viewModel.areaCouncil?.name ?: "",
            updateValue = {
                viewModel.areaCouncil = it
                viewModel.isAreaCouncilOtherSelected = it == viewModel.other
                if (!viewModel.isAreaCouncilOtherSelected) {
                    viewModel.otherAreaCouncil = ""
                    resetIslandHierarchy(viewModel)
                    viewModel.getIslandList()
                }
            },
            label = stringResource(id = R.string.area_council_mandatory),
            dropdownList = viewModel.areaCouncilList,
            errorText = stringResource(R.string.area_council_required),
            isMandatory = true,
            isEnabled = viewModel.province != null
        )

        if (viewModel.isAreaCouncilOtherSelected) {
            OtherAreaCouncilComposable(viewModel)
            OtherIslandComposable(viewModel)
            OtherVillageComposable(viewModel)
        } else {
            DropDownComposable(
                value = viewModel.island?.name ?: "",
                updateValue = {
                    viewModel.island = it
                    viewModel.isIslandOtherSelected = it == viewModel.other
                    if (!viewModel.isIslandOtherSelected) {
                        viewModel.otherIsland = ""
                        resetVillage(viewModel)
                        viewModel.getVillageList()
                    }
                },
                label = stringResource(id = R.string.island_mandatory),
                dropdownList = viewModel.islandList,
                errorText = stringResource(R.string.island_required),
                isMandatory = true,
                isEnabled = viewModel.areaCouncil != null
            )

            if (viewModel.isIslandOtherSelected) {
                OtherIslandComposable(viewModel)
                OtherVillageComposable(viewModel)
            } else {
                DropDownComposable(
                    value = viewModel.village?.name ?: "",
                    updateValue = {
                        viewModel.village = it
                        viewModel.isVillageOtherSelected = it == viewModel.other
                        if (!viewModel.isVillageOtherSelected) {
                            viewModel.otherVillage = ""
                        }
                    },
                    label = stringResource(id = R.string.village),
                    dropdownList = viewModel.villageList,
                    errorText = stringResource(R.string.village_required),
                    isMandatory = false,
                    isEnabled = viewModel.island != null
                )
                if (viewModel.isVillageOtherSelected) {
                    OtherVillageComposable(viewModel)
                }
            }
        }
    }

    PostalCodeComposable(viewModel)
}

@Composable
fun DropDownComposable(
    value: String,
    updateValue: (LevelResponse) -> Unit,
    label: String,
    dropdownList: List<LevelResponse>,
    errorText: String,
    isMandatory: Boolean,
    isEnabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            readOnly = true,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource, isEnabled) {
                    interactionSource.interactions.collect {
                        if (isEnabled && it is PressInteraction.Release) {
                            expanded = !expanded
                        }
                    }
                }
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            supportingText = {
                if (isError)
                    Text(errorText)
            },
            isError = isError
        )

        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.91f)
                .heightIn(0.dp, 300.dp),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                isError = value.isBlank() && isMandatory
            },
        ) {
            dropdownList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                        isError = false
                        updateValue(label)
                    },
                    text = {
                        Text(
                            text = label.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun OtherProvinceComposable(
    viewModel: PatientRegistrationStepThreeViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.otherProvince,
        maxLength = viewModel.maxLength,
        weight = 1f,
        isError = viewModel.otherProvinceError,
        error = stringResource(R.string.province_required),
        label = stringResource(R.string.other_province_mandatory),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        updateValue = {
            viewModel.otherProvince = it
            viewModel.otherProvinceError = viewModel.otherProvince.isBlank()
        }
    )
}

@Composable
private fun OtherAreaCouncilComposable(
    viewModel: PatientRegistrationStepThreeViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.otherAreaCouncil,
        maxLength = viewModel.maxLength,
        weight = 1f,
        isError = viewModel.otherAreaCouncilError,
        error = stringResource(R.string.area_council_required),
        label = stringResource(R.string.other_area_council_mandatory),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        updateValue = {
            viewModel.otherAreaCouncil = it
            viewModel.otherAreaCouncilError = viewModel.otherAreaCouncil.isBlank()
        }
    )
}

@Composable
private fun OtherIslandComposable(
    viewModel: PatientRegistrationStepThreeViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.otherIsland,
        maxLength = viewModel.maxLength,
        weight = 1f,
        isError = viewModel.otherIslandError,
        error = stringResource(R.string.island_required),
        label = stringResource(R.string.other_island_mandatory),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        updateValue = {
            viewModel.otherIsland = it
            viewModel.otherIslandError = viewModel.otherIsland.isBlank()
        }
    )
}

@Composable
private fun OtherVillageComposable(
    viewModel: PatientRegistrationStepThreeViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.otherVillage,
        maxLength = viewModel.maxLength,
        weight = 1f,
        isError = viewModel.otherVillageError,
        error = stringResource(R.string.village_required),
        label = stringResource(R.string.other_village_mandatory),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        updateValue = {
            viewModel.otherVillage = it
            viewModel.otherVillageError = viewModel.otherVillage.isBlank()
        }
    )
}

@Composable
private fun PostalCodeComposable(
    viewModel: PatientRegistrationStepThreeViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.postalCode,
        label = stringResource(R.string.postal_code),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.postalCodeLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.Characters,
        updateValue = {
            if ((it.matches(onlyNumbers) || it.isEmpty()))
                viewModel.postalCode = it
        }
    )
}

private fun resetAreaCouncilHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.areaCouncil = null
    viewModel.otherAreaCouncil = ""
    viewModel.isAreaCouncilOtherSelected = false
    resetIslandHierarchy(viewModel)
}

private fun resetIslandHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.island = null
    viewModel.otherIsland = ""
    viewModel.isIslandOtherSelected = false
    resetVillage(viewModel)
}

private fun resetVillage(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.village = null
    viewModel.otherVillage = ""
    viewModel.isVillageOtherSelected = false
}