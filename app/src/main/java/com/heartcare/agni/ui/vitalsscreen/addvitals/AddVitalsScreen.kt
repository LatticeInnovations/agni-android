package com.heartcare.agni.ui.vitalsscreen.addvitals

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.vital.VitalLocal
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.vitalsscreen.components.CustomChip
import com.heartcare.agni.ui.vitalsscreen.components.OptionFilterBottomSheet
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.VitalConstants
import com.heartcare.agni.utils.constants.VitalConstants.VITAL_UPDATE_OR_ADD
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbersWithDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@Composable
fun AddVitalsScreen(navController: NavController, viewModel: AddVitalsViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = viewModel.isLaunched) {
        viewModel.apply {
            if (isLaunched) {
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                vitalLocal =
                    navController.previousBackStackEntry?.savedStateHandle?.get<VitalLocal>(
                        VitalConstants.VITAL
                    )
                setVitalDetails()
                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                    )
                    Timber.d("Student: ${patient.toJson()} $appointmentResponseLocal  : ${checkIsEdit()}")

                } ?: run {
                    Timber.d("Student: null")

                }

            }
            isLaunched = true
        }

    }
    AddVitals(navController, viewModel)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVitals(navController: NavController, viewModel: AddVitalsViewModel) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                Text(
                    text = if (viewModel.vitalLocal == null) stringResource(R.string.add_vitals) else stringResource(
                        R.string.edit_vital
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag(stringResource(R.string.add_vital_title_text))
                )
            },
            actions = {
                IconButton(onClick = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        VITAL_UPDATE_OR_ADD,
                        ""
                    )
                    navController.navigateUp()
                }) {
                    Icon(
                        Icons.Filled.Close, contentDescription = stringResource(R.string.back_icon)
                    )
                }
            })
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(paddingValues = paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeightCard(viewModel = viewModel)
            WeightCard(viewModel = viewModel)
            EyeTestCard(viewModel = viewModel)
            HeartRateCard(viewModel = viewModel)
            RespiratoryCard(viewModel = viewModel)
            SpO2Card(viewModel = viewModel)
            TemperatureCard(viewModel = viewModel)
            BloodPressureCard(viewModel = viewModel)
            CholesterolTextField(viewModel = viewModel)
            BloodGlucoseCard(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            SetBottomSheets(viewModel)
        }

    }, bottomBar = {
        AnimatedVisibility(visible =  viewModel.validateVitalsDetails()) {
            SaveButton(navController, viewModel, coroutineScope, context)
        }
        })


}

@Composable
fun SaveButton(
    navController: NavController,
    viewModel: AddVitalsViewModel,
    coroutineScope: CoroutineScope,
    context: Context
) {
    Box(
        modifier = Modifier
            .padding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = if (viewModel.vitalLocal != null && viewModel.appointmentResponseLocal != null) viewModel.checkIsEdit() else getBooleanTrue(),
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(), onClick = {
                    if (!viewModel.isButtonClicked) {
                        handleNavigate(
                            viewModel, coroutineScope, navController, context
                        )
                        viewModel.isButtonClicked = true
                    }
                }, contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Text("Save")
            }
        }


    }

}

private fun handleNavigate(
    viewModel: AddVitalsViewModel,
    coroutineScope: CoroutineScope,
    navController: NavController,
    context: Context
) {
    if (viewModel.vitalLocal != null) {
        viewModel.updateVital {
            coroutineScope.launch {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    VITAL_UPDATE_OR_ADD,
                    context.getString(R.string.vitals_update_successfully)
                )
                navController.navigateUp()
            }
        }
    } else {
        viewModel.insertVital {
            coroutineScope.launch {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    VITAL_UPDATE_OR_ADD,
                    context.getString(R.string.vitals_added_successfully)
                )
                navController.navigateUp()
            }
        }
    }

}


@Composable
fun SetBottomSheets(viewModel: AddVitalsViewModel) {
    if (viewModel.isShowLeftEyeSheet) {
        OptionFilterBottomSheet(
            label = stringResource(id = R.string.left_eye),
            showSheet = { isShowSheet ->
                viewModel.isShowLeftEyeSheet = isShowSheet
            },
            selectedOption = viewModel.leftEye,
            saveSelectedOption = { type ->
                viewModel.leftEye = type
            },
            list = stringArrayResource(R.array.eye_test_option_list)
        )
    }
    if (viewModel.isShowRightEyeSheet) {
        OptionFilterBottomSheet(
            label = stringResource(id = R.string.right_eye),
            showSheet = { isShowSheet ->
                viewModel.isShowRightEyeSheet = isShowSheet
            },
            selectedOption = viewModel.rightEye,
            saveSelectedOption = { type ->
                viewModel.rightEye = type
            },
            list = stringArrayResource(R.array.eye_test_option_list)
        )
    }

}


@Composable
fun HeightCard(viewModel: AddVitalsViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.height),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.heightType == stringArrayResource(R.array.height_option_list)[0]) {
                CustomTextField(
                    value = viewModel.feet,
                    label = stringResource(R.string.feet),
                    weight = .4f, maxLength = 3,
                    isError = viewModel.isFeetNotValid, error = "",
                    keyboardType = KeyboardType.Decimal,
                    keyboardCapitalization = KeyboardCapitalization.None
                ) { feet ->
                    checkFeetValid(viewModel, feet)
                }
                CustomTextField(
                    value = viewModel.inch,
                    label = stringResource(R.string.In),
                    weight = .6f, maxLength = 4,
                    isError = viewModel.isInchNotValid, error = "",
                    keyboardType = KeyboardType.Number,
                    keyboardCapitalization = KeyboardCapitalization.None
                ) { inch ->
                    checkInchValid(viewModel, inch)
                }
                viewModel.centimeter = ""
            } else if (viewModel.heightType == stringArrayResource(R.array.height_option_list)[1]) {
                CustomTextField(
                    value = viewModel.centimeter,
                    label = stringResource(R.string.centimeter),
                    weight = .6f,
                    maxLength = 5,
                    isError = viewModel.isCmNotValid,
                    error = stringResource(R.string.height_cm_error_msg),
                    keyboardType = KeyboardType.Number,
                    keyboardCapitalization = KeyboardCapitalization.None
                ) { centimeter ->
                    checkCmValid(viewModel, centimeter)
                }
                viewModel.feet = ""
                viewModel.inch = ""
            }
            val heightTypeList = stringArrayResource(id = R.array.height_option_list)
            DropDownView(viewModel.heightType, setValue = {
                val selectedTypeIndex = heightTypeList.indexOf(viewModel.heightType)
                if (selectedTypeIndex == 0)
                    viewModel.heightType = heightTypeList[1]
                else {
                    viewModel.heightType = heightTypeList[0]
                }
                viewModel.isFeetNotValid=false
                viewModel.isInchNotValid=false
                viewModel.isCmNotValid=false
            })

        }
        AnimatedVisibility(visible = viewModel.isFeetNotValid || viewModel.isInchNotValid) {
            Text(
                modifier = modifier.padding(bottom = 8.dp),
                text = if (viewModel.isFeetNotValid) stringResource(R.string.height_error_msg) else stringResource(
                    R.string.inch_error_msg
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun WeightCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.weight),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = viewModel.weight,
                label = stringResource(R.string.weight),
                weight = .8f,
                maxLength = 5,
                isError = viewModel.isWeightNotValid,
                error = stringResource(R.string.weight_error_msg),
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.None
            ) { weight ->
                checkWeightValid(viewModel, weight)
            }
            Text(
                text = stringResource(R.string.kg),
                style = MaterialTheme.typography.bodyLarge,
            )

        }
    }
}

@Composable
fun EyeTestCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.eye_test_result),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomChip(
                idSelected = viewModel.withoutGlassChipSelected,
                label = stringResource(R.string.without_glasses)
            ) {
                viewModel.withoutGlassChipSelected = !viewModel.withoutGlassChipSelected
                if (viewModel.withoutGlassChipSelected) {
                    viewModel.withGlassChipSelected = false
                }
            }
            CustomChip(
                idSelected = viewModel.withGlassChipSelected,
                label = stringResource(R.string.with_glasses)
            ) {
                viewModel.withGlassChipSelected = !viewModel.withGlassChipSelected
                if (viewModel.withGlassChipSelected) viewModel.withoutGlassChipSelected = false

            }
        }

        EyeDropDown(value = viewModel.leftEye,
            label = stringResource(R.string.left_eye),
            onClick = { isClicked ->
                viewModel.isShowLeftEyeSheet = isClicked
            })
        EyeDropDown(value = viewModel.rightEye,
            label = stringResource(R.string.right_eye),
            onClick = { isClicked ->
                viewModel.isShowRightEyeSheet = isClicked

            })
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HeartRateCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        CustomTextField(
            value = viewModel.heartRate,
            label = stringResource(R.string.heart_rate_per_minute),
            weight = 1f,
            maxLength = 4,
            isError = viewModel.isHeartNotValid,
            error = stringResource(R.string.heart_rate_error_msg),
            keyboardType = KeyboardType.Number,
            keyboardCapitalization = KeyboardCapitalization.None
        ) { heartRate ->
            checkHRValid(viewModel, heartRate)
        }
    }
}

@Composable
fun RespiratoryCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {

        CustomTextField(
            value = viewModel.respiratoryRate,
            label = stringResource(R.string.respiratory_rate_per_minute),
            weight = 1f,
            maxLength = 4,
            isError = viewModel.isRespNotValid,
            error = stringResource(R.string.respiratory_rate_error_msg),
            keyboardType = KeyboardType.Number,
            keyboardCapitalization = KeyboardCapitalization.None
        ) { respiratoryRate ->
            checkRespValid(viewModel, respiratoryRate)
        }
    }
}

@Composable
fun SpO2Card(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {

        CustomTextField(
            value = viewModel.spo2,
            label = stringResource(R.string.spo2_lable),
            weight = 1f,
            maxLength = 4,
            isError = viewModel.isSpo2NotValid,
            error = stringResource(R.string.spo2_error_msg),
            keyboardType = KeyboardType.Number,
            keyboardCapitalization = KeyboardCapitalization.None
        ) { spo2 ->
            checkSpo2Valid(viewModel, spo2)
        }
    }
}

@Composable
fun TemperatureCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = viewModel.temperature,
                label = stringResource(id = R.string.temperature),
                weight = .6f,
                maxLength = if (viewModel.temperatureType == stringArrayResource(id = R.array.temperature_option_list)[0])5 else 4,
                isError = viewModel.isTempNotValid,
                error = if (viewModel.temperatureType == stringArrayResource(id = R.array.temperature_option_list)[0]) stringResource(
                    R.string.fahrenheit_error_msg
                ) else stringResource(
                    R.string.celsius_error_msg
                ),
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.None
            ) { temperature ->
                checkTempValid(viewModel, temperature, context)
            }

            val typeList = stringArrayResource(id = R.array.temperature_option_list)
            DropDownView(viewModel.temperatureType, setValue = {
                val selectedTypeIndex = typeList.indexOf(viewModel.temperatureType)
                if (selectedTypeIndex == 0)
                    viewModel.temperatureType = typeList[1]
                else {
                    viewModel.temperatureType = typeList[0]
                }
                viewModel.temperature = ""
                viewModel.isTempNotValid = false

            })

        }
    }
}

@Composable
fun BloodPressureCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.blood_pressure),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = viewModel.bpSystolic, label = stringResource(R.string.systolic),
                weight = .4f,
                maxLength = 4, isError = viewModel.isSystolicNotValid, error = "",
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.None
            ) { bpSystolic ->
                checkBpSystolicValid(viewModel, bpSystolic)
            }
            CustomTextField(
                value = viewModel.bpDiastolic, label = stringResource(R.string.diastolic),
                weight = .7f,
                maxLength = 4, isError = viewModel.isDiastolicNotValid, error = "",
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.None
            ) { bpDiastolic ->
                checkBpDiastolicValid(viewModel, bpDiastolic)
            }

            Text(
                text = stringResource(R.string.mmhg),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(.9f)
            )

        }
        AnimatedVisibility(visible = viewModel.isDiastolicNotValid || viewModel.isSystolicNotValid) {
            Text(
                modifier = modifier.padding(bottom = 8.dp),
                text = if (viewModel.isDiastolicNotValid) stringResource(R.string.bpDiastolic_error_msg) else stringResource(
                    R.string.bpSystolic_error_msg
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

    }
}

@Composable
private fun CholesterolTextField(viewModel: AddVitalsViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(7f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.cholesterol,
                    onValueChange = { value ->
                        checkCholesterolValid(viewModel, value)
                    },
                    label = {
                        Text(stringResource(R.string.total_cholestrol))
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = viewModel.cholesterolError,
                    supportingText = if (viewModel.cholesterolError) {
                        {
                            Text(
                                if (viewModel.selectedCholesterolIndex == 0)
                                    stringResource(
                                        R.string.value_cannot_exceed,
                                        "1.0",
                                        "13.0",
                                        "mmol/L"
                                    )
                                else
                                    stringResource(
                                        R.string.value_cannot_exceed,
                                        "1",
                                        "500",
                                        "mg/dL"
                                    )
                            )
                        }
                    } else null,
                    singleLine = true
                )
            }
            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = viewModel.cholesterolUnits[viewModel.selectedCholesterolIndex],
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.swap),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (viewModel.selectedCholesterolIndex == 0) viewModel.selectedCholesterolIndex =
                            1
                        else viewModel.selectedCholesterolIndex = 0
                        viewModel.cholesterolError = false
                        viewModel.cholesterol = ""
                    }
                )
            }
        }
    }
}

@Composable
fun BloodGlucoseCard(modifier: Modifier = Modifier, viewModel: AddVitalsViewModel) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
            .padding(start = 16.dp, end = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.blood_glucose),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        BgChip(modifier, viewModel)

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = viewModel.bloodGlucose,
                label = "${stringResource(id = if (viewModel.bgRandomChipSelected) R.string.random else R.string.fasting)} ${
                    stringResource(
                        id = R.string.blood_glucose
                    ).lowercase()
                }",
                weight = .7f,
                maxLength = 4,
                isError = viewModel.isBgNotValid,
                error = if (viewModel.bgType == stringArrayResource(id = R.array.bg_option_list)[0]) stringResource(
                    R.string.bg_mg_error_msg
                ) else stringResource(R.string.bg_mmol_error_msg),
                keyboardType = KeyboardType.Number,
                keyboardCapitalization = KeyboardCapitalization.None
            ) { bloodGlucose ->
                checkBgValid(viewModel, bloodGlucose, context)
            }
            Spacer(modifier = Modifier.width(8.dp))
            val typeList = stringArrayResource(id = R.array.bg_option_list)
            DropDownView(viewModel.bgType, setValue = {
                val selectedTypeIndex = typeList.indexOf(viewModel.bgType)
                if (selectedTypeIndex == 0)
                    viewModel.bgType = typeList[1]
                else {
                    viewModel.bgType = typeList[0]
                }
                viewModel.bloodGlucose = ""
                viewModel.isBgNotValid = false
            })
        }

    }
}

@Composable
fun BgChip(modifier: Modifier, viewModel: AddVitalsViewModel) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomChip(
            idSelected = viewModel.bgRandomChipSelected, label = stringResource(R.string.random)
        ) {
            viewModel.bgRandomChipSelected = !viewModel.bgRandomChipSelected
            if (viewModel.bgRandomChipSelected) {
                viewModel.bgFastingChipSelected = false
            }
        }
        CustomChip(
            idSelected = viewModel.bgFastingChipSelected,
            label = stringResource(R.string.fasting)
        ) {
            viewModel.bgFastingChipSelected = !viewModel.bgFastingChipSelected
            if (viewModel.bgFastingChipSelected) viewModel.bgRandomChipSelected = false

        }
    }
}

@Composable
fun DropDownView(value: String, setValue: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            painter = painterResource(R.drawable.swap),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                setValue()

            }
        )
    }


}

@Composable
fun EyeDropDown(value: String, label: String, onClick: (Boolean) -> Unit) {
    Box(
        modifier = Modifier.wrapContentWidth()
    ) {
        OutlinedTextField(value = value, onValueChange = {}, label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }, modifier = Modifier.fillMaxWidth(), readOnly = true, trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }, interactionSource = remember {
            MutableInteractionSource()
        }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release) {
                        onClick(true)
                    }
                }
            }
        })
    }
}

fun checkFeetValid(viewModel: AddVitalsViewModel, hFeet: String) {
    viewModel.apply {
        feet = hFeet.trim().filter {
            it.isDigit()
        }
        isFeetNotValid = if (feet.isNotBlank()) {
            feet.toInt() < 1 || feet.toInt() > 8
        } else {
            false
        }

    }
}

fun checkInchValid(viewModel: AddVitalsViewModel, hInch: String) {
    viewModel.apply {
        if (hInch.isBlank() || (hInch.matches(onlyNumbersWithDecimal) && hInch.length <= 4)) {
            inch = hInch
            isInchNotValid = inch.isNotBlank() &&
                    inch.toDouble() !in 0.0..11.9
        }

    }
}

fun checkCmValid(viewModel: AddVitalsViewModel, hCentimeter: String) {
    viewModel.apply {
        if (hCentimeter.isBlank() || (hCentimeter.matches(onlyNumbersWithDecimal) && hCentimeter.length <= 5)) {
            centimeter = hCentimeter
            isCmNotValid = centimeter.isNotBlank() &&
                    centimeter.toDouble() !in 30.0..250.0
        }

    }


}

fun checkWeightValid(viewModel: AddVitalsViewModel, sWeight: String) {
    viewModel.apply {
        if (sWeight.isBlank() || (sWeight.matches(onlyNumbersWithDecimal) && sWeight.length <= 5)) {
            weight = sWeight
            isWeightNotValid = weight.isNotBlank() &&
                    weight.toDouble() !in 1.0..200.0
        }
    }
}

fun checkHRValid(viewModel: AddVitalsViewModel, hRate: String) {
    viewModel.apply {
        heartRate = hRate.trim().filter { it.isDigit() }
        isHeartNotValid = if (heartRate.isNotBlank()) {
            heartRate.toInt() < 60 || heartRate.toInt() > 200
        } else {
            false
        }

    }

}

fun checkRespValid(viewModel: AddVitalsViewModel, resp: String) {
    viewModel.apply {
        respiratoryRate = resp.trim().filter { it.isDigit() }
        isRespNotValid = if (respiratoryRate.isNotBlank()) {
            respiratoryRate.toInt() < 12 || respiratoryRate.toInt() > 60
        } else {
            false
        }

    }

}

fun checkSpo2Valid(viewModel: AddVitalsViewModel, sp: String) {
    viewModel.apply {
        spo2 = sp.trim().filter { it.isDigit() }
        isSpo2NotValid = if (spo2.isNotBlank()) {
            spo2.toInt() < 90 || spo2.toInt() > 100
        } else {
            false
        }

    }

}

fun checkTempValid(viewModel: AddVitalsViewModel, temp: String, context: Context) {
    viewModel.apply {
        if (viewModel.temperatureType.lowercase() == context.getString(R.string.fahrenheit)
                .lowercase()
        ) {
            if (temp.isBlank() || (temp.matches(onlyNumbersWithDecimal) && temp.length <= 5)) {
                temperature = temp
                isTempNotValid = temperature.isNotBlank() &&
                        temperature.toDouble() !in 91.8..105.5
            }
        } else {
            if (temp.isBlank() || (temp.matches(onlyNumbersWithDecimal) && temp.length <= 4)) {
                temperature = temp
                isTempNotValid = temperature.isNotBlank() &&
                        temperature.toDouble() !in 33.2..41.0
            }
        }

    }

}
fun checkCholesterolValid(viewModel: AddVitalsViewModel, value: String) {
    viewModel.apply {
        if (viewModel.selectedCholesterolIndex == 0) {
            if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
                viewModel.cholesterol = value
                viewModel.cholesterolError = viewModel.cholesterol.isNotBlank() &&
                        viewModel.cholesterol.toDouble() !in 1.0..13.0
            }
        } else {
            if (value.isBlank() || (value.matches(onlyNumbers) && value.length < 4)) {
                viewModel.cholesterol = value
                viewModel.cholesterolError = viewModel.cholesterol.isNotBlank() &&
                        viewModel.cholesterol.toInt() !in 1..500
            }
        }

    }
}

fun checkBpSystolicValid(viewModel: AddVitalsViewModel, systolic: String) {
    viewModel.apply {
        bpSystolic = systolic.trim().filter { it.isDigit() }
        isSystolicNotValid = if (bpSystolic.isNotBlank()) {
            bpSystolic.toInt() < 30 || bpSystolic.toInt() > 300
        } else {
            false
        }
        if (bpSystolic.isNotBlank() && !isSystolicNotValid && bpDiastolic.isBlank()) {
            isDiastolicNotValid = true
        } else if (bpSystolic.isBlank() && bpDiastolic.isBlank()) {
            isSystolicNotValid = false
            isDiastolicNotValid = false
        }
    }

}

fun checkBpDiastolicValid(viewModel: AddVitalsViewModel, diastolic: String) {
    viewModel.apply {
        bpDiastolic = diastolic.trim().filter { it.isDigit() }
        isDiastolicNotValid = if (bpDiastolic.isNotBlank()) {
            bpDiastolic.toInt() < 20 || bpDiastolic.toInt() > 200
        } else {
            false
        }
        if (bpDiastolic.isNotBlank() && !isDiastolicNotValid && bpSystolic.isBlank()) {
            isSystolicNotValid = true
        } else if (bpSystolic.isBlank() && bpDiastolic.isBlank()) {
            isSystolicNotValid = false
            isDiastolicNotValid = false
        }
    }

}

fun checkBgValid(viewModel: AddVitalsViewModel, bg: String, context: Context) {
    viewModel.apply {
        if (viewModel.bgType.lowercase() != context.getString(R.string.mg_dl).lowercase()) {
            if (bg.isBlank() || (bg.matches(onlyNumbersWithDecimal) && bg.length < 5)) {
                viewModel.bloodGlucose = bg
                viewModel.isBgNotValid = viewModel.bloodGlucose.isNotBlank() &&
                        viewModel.bloodGlucose.toDouble() !in 1.1..33.4
            }
        } else {
            if (bg.isBlank() || (bg.matches(onlyNumbers) && bg.length < 4)) {
                viewModel.bloodGlucose = bg
                viewModel.isBgNotValid = viewModel.bloodGlucose.isNotBlank() &&
                        viewModel.bloodGlucose.toInt() !in 20..600
            }
        }

    }


}

fun getBooleanTrue(): Boolean {
    return true
}



