package com.latticeonfhir.android.ui.vitalsscreen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.data.Entry
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.CustomDialog
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.ui.theme.VitalLabel
import com.latticeonfhir.android.ui.vitalsscreen.components.CustomChip
import com.latticeonfhir.android.ui.vitalsscreen.components.LineChartView
import com.latticeonfhir.android.ui.vitalsscreen.components.LineChartViewGlucose
import com.latticeonfhir.android.ui.vitalsscreen.components.SegmentedButtonForVital
import com.latticeonfhir.android.ui.vitalsscreen.enums.BGEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.TemperatureEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.VitalsEyeEnum
import com.latticeonfhir.android.ui.vitalsscreen.enums.VitalsTrendEnum
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.VitalConstants.ALL
import com.latticeonfhir.android.utils.constants.VitalConstants.CVD_RECORD
import com.latticeonfhir.android.utils.constants.VitalConstants.LIST_TYPE_CVD
import com.latticeonfhir.android.utils.constants.VitalConstants.LIST_TYPE_VITAL
import com.latticeonfhir.android.utils.constants.VitalConstants.VITAL_UPDATE_OR_ADD
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertDateFormat
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertedDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.formatDateToDayMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(navController: NavController, vitalsViewModel: VitalsViewModel = hiltViewModel()) {

    HandleLaunchedEffect(vitalsViewModel, navController)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON"
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.vitals),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("VITAL_TITLE_TEXT")
                )
            })
    }, content = { paddingValues ->
            ShowGraphAndList(vitalsViewModel, paddingValues, context)
    }, bottomBar = {
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
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(), onClick = {
                        vitalsViewModel.getAppointmentInfo {
                            if (vitalsViewModel.canAddAssessment) {
                                scope.launch {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        key = PATIENT, vitalsViewModel.patient
                                    )
                                    navController.navigate(Screen.AddVitalsScreen.route)
                                }

                            } else if (vitalsViewModel.isAppointmentCompleted) {
                                vitalsViewModel.showAppointmentCompletedDialog = true
                            } else {
                                vitalsViewModel.showAddToQueueDialog = true
                            }
                        }

                    },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_icon),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        stringResource(id = R.string.add_vitals)
                    )
                }
            }


        }

    })

    LaunchedEffect(key1 = vitalsViewModel.msg) {
        if (vitalsViewModel.msg.isNotBlank()) {
            snackBarHostState.showSnackbar(vitalsViewModel.msg)
            vitalsViewModel.msg = ""
        }

    }
    ShowDialogs(vitalsViewModel, navController, scope)
}

@Composable
fun ShowGraphAndList(
    vitalsViewModel: VitalsViewModel, paddingValues: PaddingValues, context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (vitalsViewModel.isVitalExist || vitalsViewModel.previousRecords.isNotEmpty()) {
            val vitals by vitalsViewModel._vitals.collectAsState()
            val combinedList = getCombinedList(vitalsViewModel, vitals)
            LazyColumn(userScrollEnabled = true) {
                item {
                    VitalsTrendGraph(vitalsViewModel)
                }
                item {
                    SegmentedButtonForVital(options = listOf(ALL, CVD_RECORD),
                        isCVDListEmpty = vitalsViewModel.previousRecords.isEmpty(),
                        selectedOption = vitalsViewModel.selectedOption,
                        onOptionSelected = { vitalsViewModel.selectedOption = it })
                }
                items(if (vitalsViewModel.selectedOption == CVD_RECORD) combinedList.filter { it.type == LIST_TYPE_CVD } else combinedList) { item ->
                    when (item.type) {
                        LIST_TYPE_VITAL -> VitalsCardLayout(
                            vital = item.content as VitalLocal, context = context
                        )

                        LIST_TYPE_CVD -> CVDRecordCardLayout(
                            cvdResponse = item.content as CVDResponse
                        )
                    }

                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_record_found),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )

            }
        }
    }

}

fun getCombinedList(
    vitalsViewModel: VitalsViewModel, vitals: List<VitalLocal>
): List<CombineVitalAndCVDRecord> {
    return (vitals.map { vital ->
        CombineVitalAndCVDRecord(
            LIST_TYPE_VITAL, vital.createdOn, vital
        )
    } + vitalsViewModel.previousRecords.map {
        CombineVitalAndCVDRecord(
            LIST_TYPE_CVD, it.createdOn, it
        )
    }).sortedBy { it.date }.reversed()
}

@Composable
fun HandleLaunchedEffect(vitalsViewModel: VitalsViewModel, navController: NavController) {
    LaunchedEffect(key1 = vitalsViewModel.isLaunched) {
        vitalsViewModel.apply {
            if (isLaunched) {
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                    )
                    msg = navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                        VITAL_UPDATE_OR_ADD
                    ) ?: ""
                    getVitals()
                    getRecords()

                    Timber.d("Student:  ${_vitals.toJson()} : ${patient.toJson()} $appointmentResponseLocal")
                } ?: run {
                    Timber.d("Student: null")

                }

            }
            isLaunched = true

        }

    }

}

@Composable
private fun ShowDialogs(
    vitalsViewModel: VitalsViewModel, navController: NavController, scope: CoroutineScope
) {
    if (vitalsViewModel.showAddToQueueDialog) {
        CustomDialog(title = if (vitalsViewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
            id = R.string.add_to_queue_question
        ),
            text = stringResource(id = R.string.add_to_queue_vital_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (vitalsViewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { vitalsViewModel.showAddToQueueDialog = false },
            confirm = {
                if (vitalsViewModel.appointment != null) {
                    vitalsViewModel.updateStatusToArrived(
                        vitalsViewModel.patient!!, vitalsViewModel.appointment!!
                    ) {
                        vitalsViewModel.showAddToQueueDialog = false
                        scope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                key = PATIENT, vitalsViewModel.patient
                            )
                            navController.navigate(Screen.AddVitalsScreen.route)
                        }

                    }
                } else {
                    if (vitalsViewModel.ifAllSlotsBooked) {
                        vitalsViewModel.showAllSlotsBookedDialog = true
                    } else {
                        vitalsViewModel.addPatientToQueue(vitalsViewModel.patient!!) {
                            vitalsViewModel.showAddToQueueDialog = false

                            scope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = PATIENT, vitalsViewModel.patient
                                )
                                navController.navigate(Screen.AddVitalsScreen.route)
                            }
                        }


                    }
                }
            })
    }
    if (vitalsViewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            vitalsViewModel.showAllSlotsBookedDialog = false
        }
    }
}


@Composable
fun VitalsTrendGraph(vitalsViewModel: VitalsViewModel, modifier: Modifier = Modifier) {
    val list by vitalsViewModel._vitals.collectAsState()

    if (list.filter { it.weight != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 || list.filter { it.heartRate != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 || list.filter { it.respRate != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 || list.filter { it.spo2 != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 || list.filter { it.bloodGlucose != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 || getBPListSize(
            list,
            vitalsViewModel
        ) > 1
    ) {
        ShowTrendGraphCard(modifier, vitalsViewModel, list.reversed())
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceBright
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .testTag("VITAL_TITLE_TEXT")
                    .padding(20.dp),
                text = stringResource(R.string.graph_empty_error_msg),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowTrendGraphCard(
    modifier: Modifier,
    vitalsViewModel: VitalsViewModel,
    list: List<VitalLocal>
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
    ) {
        Text(
            modifier = Modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.vitals_trend),
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
            )
        )
        LaunchedEffect(key1 = vitalsViewModel.isFistLaunch) {
            if (!vitalsViewModel.isFistLaunch) {
                showChipSelection(vitalsViewModel, list)
                vitalsViewModel.isFistLaunch = true
            }

        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(visible = list.filter { it.weight != null }
                .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1) {
                CustomChip(
                    idSelected = vitalsViewModel.isWeightSelected,
                    label = VitalsTrendEnum.Weight.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = true
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }
            }
            AnimatedVisibility(visible = list.filter { it.heartRate != null }
                .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1) {
                CustomChip(
                    idSelected = vitalsViewModel.isHRSelected, label = VitalsTrendEnum.HR.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = true
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }
            }

            AnimatedVisibility(visible = list.filter { it.respRate != null }
                .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1) {
                CustomChip(
                    idSelected = vitalsViewModel.isRRSelected, label = VitalsTrendEnum.RR.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = true
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }
            }
            AnimatedVisibility(visible = list.filter { it.spo2 != null }
                .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1) {

                CustomChip(
                    idSelected = vitalsViewModel.isSpO2Selected, label = VitalsTrendEnum.SpO2.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = true
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }

            }
            AnimatedVisibility(visible = list.filter { it.bloodGlucose != null && it.bloodGlucoseType == BGEnum.RANDOM.value || it.bloodGlucoseType == BGEnum.FASTING.value }
                .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1) {

                CustomChip(
                    idSelected = vitalsViewModel.isGlucoseSelected,
                    label = VitalsTrendEnum.Glucose.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = true
                        isBPSelected = false
                    }
                }
            }
            Timber.d("SIZE: ${getBPListSize(list, vitalsViewModel)}")
            AnimatedVisibility(visible = getBPListSize(list, vitalsViewModel) > 1) {
                CustomChip(
                    idSelected = vitalsViewModel.isBPSelected, label = VitalsTrendEnum.BP.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = true
                    }

                }
            }

        }


        AnimatedVisibility(vitalsViewModel.isGlucoseSelected) {
            GraphLegendGroup(
                legendList = listOf(
                    Pair(BGEnum.RANDOM.value.capitalizeFirstLetter(), VitalLabel),
                    Pair(
                        BGEnum.FASTING.value.capitalizeFirstLetter(),
                        MaterialTheme.colorScheme.primary
                    )

                )
            )

        }
        AnimatedVisibility(vitalsViewModel.isBPSelected) {
            GraphLegendGroup(
                legendList = listOf(
                    Pair(stringResource(id = R.string.systolic), VitalLabel),
                    Pair(stringResource(id = R.string.diastolic), MaterialTheme.colorScheme.primary)

                )
            )
        }

        if (!vitalsViewModel.isGlucoseSelected && (vitalsViewModel.isWeightSelected || vitalsViewModel.isHRSelected || vitalsViewModel.isRRSelected || vitalsViewModel.isSpO2Selected || vitalsViewModel.isBPSelected)) {
            LineChartView(modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                .height(200.dp),
                entries1 = getChartEntries(list, vitalsViewModel),
                entries2 = getChartEntries2(list, vitalsViewModel),
                labels = getLabels(vitalsViewModel, list),
                isBp = vitalsViewModel.isBPSelected
            )
            Timber.d("List : ${list.map { it.createdOn.formatDateToDayMonth() }}")
        } else if (vitalsViewModel.isGlucoseSelected) {
            LineChartViewGlucose(modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                .height(200.dp),
                entriesRandom = getChartEntries2(list, vitalsViewModel),
                entriesFasting = getChartEntries(list, vitalsViewModel),
                labels = getLabels(vitalsViewModel, list)
            )
        }
    }


}

fun getLabels(vitalsViewModel: VitalsViewModel, list: List<VitalLocal>): List<String> {
    return if (!vitalsViewModel.isBPSelected) {
        (list.map { it.createdOn }).distinct().sorted().map { it.formatDateToDayMonth() }.distinct()
    } else {
        val vitalDates = list.map { it.createdOn }
        val cvdDates = vitalsViewModel.previousRecords.map { it.createdOn }

        // Combine and sort the dates
        (vitalDates + cvdDates).distinct().sorted().map { it.formatDateToDayMonth() }.distinct()
    }
}

fun getBPListSize(list: List<VitalLocal>, vitalsViewModel: VitalsViewModel): Int {
    return list.filter { it.bpSystolic != null }
        .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size + vitalsViewModel.previousRecords.groupBy { it.createdOn.formatDateToDayMonth() }.keys.size
}

@Composable
fun GraphLegendGroup(legendList: List<Pair<String, Color>>) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Iterate through each legend item (text and color pair)
            legendList.forEach { (label, color) ->
                IndicatorWithText(color = color, text = label)

                Spacer(modifier = Modifier.width(8.dp)) // Spacer between legend items
            }
        }
    }
}

@Composable
fun IndicatorWithText(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Circle indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Text next to the circle
        Text(
            text = text, color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


private fun String.capitalizeFirstLetter(): String {
    return this.lowercase().replaceFirstChar { it.uppercaseChar() }
}

fun getChartEntries(list: List<VitalLocal>, vitalsViewModel: VitalsViewModel): List<Entry>? {

    return when {
        vitalsViewModel.isWeightSelected -> {
            getEntries(list) { it.weight?.toFloat() }
        }
        vitalsViewModel.isHRSelected -> {
            getEntries(list) { it.heartRate?.toFloat() }
        }

        vitalsViewModel.isRRSelected -> {
            getEntries(list) { it.respRate?.toFloat() }
        }

        vitalsViewModel.isSpO2Selected -> {
            getEntries(list) { it.spo2?.toFloat() }

        }

        vitalsViewModel.isGlucoseSelected -> {
            getEntries(
                list.filter { it.bloodGlucoseType == BGEnum.FASTING.value }, list
            ) {
                if (it.bloodGlucoseUnit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose?.toFloat()
                    ?.times(18.018f)
                else it.bloodGlucose?.toFloat()
            }
        }

        vitalsViewModel.isBPSelected -> {
            getCombinedEntries(vitalList = list,
                cvdList = vitalsViewModel.previousRecords,
                vitalValueSelector = { it.bpSystolic?.toFloat() },
                cvdValueSelector = { it.bpSystolic.toFloat() })
        }

        else -> {
            null
        }
    }

}



fun getChartEntries2(list: List<VitalLocal>, vitalsViewModel: VitalsViewModel): List<Entry>? {

    return if (vitalsViewModel.isGlucoseSelected) {
        getEntries(
            list.filter { it.bloodGlucoseType == BGEnum.RANDOM.value }, list
        ) {
            if (it.bloodGlucoseUnit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose?.toFloat()
                ?.times(18.018f)?.roundToInt()?.toFloat()
            else it.bloodGlucose?.toFloat()
        }
    } else if (vitalsViewModel.isBPSelected) {
        getCombinedEntries(vitalList = list,
            cvdList = vitalsViewModel.previousRecords,
            vitalValueSelector = { it.bpDiastolic?.toFloat() },
            cvdValueSelector = { it.bpDiastolic.toFloat() })

    } else {
        null
    }

}


private fun getEntries(
    list: List<VitalLocal>, bgList: List<VitalLocal>? = null, valueSelector: (VitalLocal) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group the list by formatted dates
    val filteredList = list.groupBy { it.createdOn.formatDateToDayMonth() }
    val vitalGroupedByDate = bgList?.groupBy { it.createdOn.formatDateToDayMonth() }
        ?: list.groupBy { it.createdOn.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    (vitalGroupedByDate.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }.also { allDates ->
            Timber.d("Date: $allDates")
            for (date in allDates) {

                // Find the index of the date in the labels list
                val labelIndex = allDates.indexOf(date)
                if (labelIndex != -1) {
                    Timber.d(
                        "Value: $labelIndex: $date :\n${
                            vitalGroupedByDate[date]?.mapNotNull(
                                valueSelector
                            ).orEmpty()
                        }"
                    )
                    // Calculate the average value for the grouped records

                    val values = if (bgList != null) filteredList[date]?.mapNotNull(valueSelector)
                        .orEmpty() else vitalGroupedByDate[date]?.mapNotNull(valueSelector)
                        .orEmpty()
                    if (values.isNotEmpty()) {
                        // Calculate the average only if values are present
                        val averageValue = values.average().toFloat()
                        // Add the average value as an Entry for the graph
                        mutableList.add(
                            Entry(
                                labelIndex.toFloat(),
                                averageValue.roundToInt().toFloat()
                            )
                        )
                    }
                }
            }

        }

    return mutableList
}


private fun getCombinedEntries(
    vitalList: List<VitalLocal>,
    cvdList: List<CVDResponse>,
    vitalValueSelector: (VitalLocal) -> Float?,
    cvdValueSelector: (CVDResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group both lists by formatted dates
    val vitalGroupedByDate = vitalList.groupBy { it.createdOn.formatDateToDayMonth() }
    val cvdGroupedByDate = cvdList.groupBy { it.createdOn.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    val allDates = (vitalGroupedByDate.keys + cvdGroupedByDate.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }


    for (date in allDates) {
        val labelIndex = allDates.indexOf(date)
        if (labelIndex != -1) {
            // Collect all values from both VitalLocal and CVDResponse for this date
            val vitalValues = vitalGroupedByDate[date]?.mapNotNull(vitalValueSelector).orEmpty()
            val cvdValues = cvdGroupedByDate[date]?.mapNotNull(cvdValueSelector).orEmpty()

            // Combine all values into a single list
            val combinedValues = vitalValues + cvdValues

            // Calculate the average of the combined values
            if (combinedValues.isNotEmpty()) {
                val finalAverage = combinedValues.average().toFloat()
                mutableList.add(Entry(labelIndex.toFloat(), finalAverage.roundToInt().toFloat()))
            }
        }
    }

    return mutableList
}





@Composable
fun VitalsCardLayout(
    modifier: Modifier = Modifier, vital: VitalLocal, context: Context
) {
    var isViewMore by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = vital.createdOn.convertedDate().convertDateFormat(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                text = vital.practitionerName ?: stringResource(id = R.string.dash),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
                )
            )
            VitalCardItem(
                title = stringResource(id = R.string.height), value = setHeight(vital)
            )
            VitalCardItem(
                title = stringResource(id = R.string.weight),
                value = if (vital.weight != null) "${vital.weight} ${
                    stringResource(
                        id = R.string.kg
                    ).lowercase()
                }" else stringResource(id = R.string.dashes)
            )
            VitalCardItem(
                title = stringResource(R.string.hear_rate_min),
                value = vital.heartRate ?: stringResource(id = R.string.dashes)
            )
            VitalCardItem(
                title = stringResource(R.string.respiratory_rate_min),
                value = vital.respRate ?: stringResource(id = R.string.dashes)
            )
            VitalCardItem(
                title = stringResource(id = R.string.temperature),
                value = if (vital.temp != null) "${vital.temp} ${if (vital.tempUnit == TemperatureEnum.CELSIUS.value) "°C" else "F"}" else stringResource(
                    id = R.string.dashes
                )
            )
            AnimatedVisibility(visible = isViewMore) {
                ViewMoreItems(vital, context)
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        isViewMore = !isViewMore
                    },
                text = if (isViewMore) stringResource(R.string.view_less) else stringResource(R.string.view_more),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

    }
}

@Composable
fun CVDRecordCardLayout(
    modifier: Modifier = Modifier, cvdResponse: CVDResponse
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {

                    Text(
                        text = cvdResponse.createdOn.convertedDate().convertDateFormat(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer

                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = cvdResponse.practitionerName ?: stringResource(id = R.string.dash),
                        style = MaterialTheme.typography.bodySmall
                    )

                }
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .width(8.dp)
                )
                CustomChip(idSelected = false, label = "CVD record", color = MaterialTheme.colorScheme.onSurfaceVariant) {

                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 16.dp, end = 16.dp, bottom = 8.dp
                )
            )
            VitalCardItem(
                title = stringResource(id = R.string.blood_pressure),
                value = if (cvdResponse.bpDiastolic != 0 && cvdResponse.bpSystolic != 0) "${cvdResponse.bpSystolic}/${cvdResponse.bpDiastolic} ${
                    stringResource(
                        id = R.string.mmhg
                    )
                }" else stringResource(id = R.string.dashes)
            )

            VitalCardItem(
                title = stringResource(id = R.string.total_cholestrol),
                value = if (cvdResponse.cholesterol != null) "${cvdResponse.cholesterol} ${cvdResponse.cholesterolUnit}" else stringResource(
                    id = R.string.dashes
                )
            )
            VitalCardItem(
                title = stringResource(id = R.string.height), value = setCVDHeight(cvdResponse)
            )
            VitalCardItem(
                title = stringResource(id = R.string.weight),
                value = if (cvdResponse.weight != null) "${cvdResponse.weight} ${
                    stringResource(
                        id = R.string.kg
                    ).lowercase()
                }" else stringResource(id = R.string.dashes)
            )

            VitalCardItem(
                title = stringResource(R.string.bmi_label),
                value = cvdResponse.bmi?.toInt()?.toString() ?: stringResource(id = R.string.dashes)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }

    }
}

@Composable
fun setHeight(vital: VitalLocal): String {
    return when {
        vital.heightCm != null -> {
            "${vital.heightCm} ${stringResource(id = R.string.cm)}"
        }

        vital.heightFt != null || vital.heightInch != null -> {
            "${vital.heightFt ?: ""}.${vital.heightInch ?: "0"} ${
                stringResource(
                    id = R.string.ft_in
                )
            }"
        }

        else -> {
            stringResource(
                id = R.string.dashes
            )
        }
    }

}

@Composable
fun setCVDHeight(cvdResponse: CVDResponse): String {
    return when {
        cvdResponse.heightCm != null -> {
            "${cvdResponse.heightCm} ${stringResource(id = R.string.cm)}"
        }

        cvdResponse.heightFt != null || cvdResponse.heightInch != null -> {
            "${cvdResponse.heightFt ?: ""}.${cvdResponse.heightInch ?: "0"} ${
                stringResource(
                    id = R.string.ft_in
                )
            }"
        }

        else -> {
            stringResource(
                id = R.string.dashes
            )
        }
    }

}

@Composable
fun ViewMoreItems(vital: VitalLocal, context: Context) {
    Column {
        VitalCardItem(
            title = stringResource(R.string.eye_test_result_left),
            value = if (vital.leftEye != null) vital.leftEye.toInt()
                .getEyeTypeName(context) else stringResource(
                id = R.string.dashes
            )
        )
        VitalCardItem(
            title = stringResource(R.string.eye_test_result_right),
            value = if (vital.rightEye != null) vital.rightEye.toInt()
                .getEyeTypeName(context) else stringResource(
                id = R.string.dashes
            )
        )
        VitalCardItem(
            title = stringResource(R.string.spo2_title), value = vital.spo2 ?: stringResource(
                id = R.string.dashes
            )
        )
        VitalCardItem(
            title = stringResource(id = R.string.blood_pressure),
            value = if (vital.bpDiastolic != null && vital.bpSystolic != null) "${vital.bpSystolic}/${vital.bpDiastolic} ${
                stringResource(
                    id = R.string.mmhg
                )
            }" else stringResource(id = R.string.dashes)
        )
        VitalCardItem(
            title = stringResource(id = R.string.total_cholestrol),
            value = if (vital.cholesterol != null) "${vital.cholesterol} ${vital.cholesterolUnit}" else stringResource(
                id = R.string.dashes
            )
        )
        VitalCardItem(
            title = "${
                stringResource(
                    id = if (vital.bloodGlucoseType == stringResource(
                            id = R.string.fasting
                        ).lowercase()
                    ) R.string.fasting else R.string.random
                )
            } ${
                stringResource(
                    id = R.string.blood_glucose
                ).lowercase()
            }",
            value = if (vital.bloodGlucose != null) "${vital.bloodGlucose} ${vital.bloodGlucoseUnit}"
            else stringResource(R.string.dashes)
        )

    }


}

@Composable
fun VitalCardItem(modifier: Modifier = Modifier, title: String, value: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 8.dp),
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            modifier = modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 8.dp),
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
private fun Int.getEyeTypeName(context: Context): String {
    return when (this) {
        VitalsEyeEnum.NORMAL_VISION.number -> VitalsEyeEnum.NORMAL_VISION.value.extractStringInBrackets()
        VitalsEyeEnum.MILD_IMPAIRMENT.number -> VitalsEyeEnum.MILD_IMPAIRMENT.value.extractStringInBrackets()
        VitalsEyeEnum.MODERATE_IMPAIRMENT.number -> VitalsEyeEnum.MODERATE_IMPAIRMENT.value.extractStringInBrackets()
        VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.number -> VitalsEyeEnum.SIGNIFICANT_IMPAIRMENT.value.extractStringInBrackets()
        VitalsEyeEnum.SEVERE_IMPAIRMENT.number -> VitalsEyeEnum.SEVERE_IMPAIRMENT.value.extractStringInBrackets()
        VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.number -> VitalsEyeEnum.VERY_SEVERE_IMPAIRMENT.value.extractStringInBrackets()
        VitalsEyeEnum.LEGAL_BLINDNESS.number -> VitalsEyeEnum.LEGAL_BLINDNESS.value.extractStringInBrackets()
        else -> {
            context.getString(R.string.dashes)
        }
    }
}

fun String.extractStringInBrackets(): String {
    val regex = "\\(([^)]+)\\)".toRegex()
    val matchResult = regex.find(this)
    return matchResult?.groups?.get(1)?.value ?: ""
}

private fun showChipSelection(vitalsViewModel: VitalsViewModel, list: List<VitalLocal>) {
    when {
        list.filter { it.weight != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isWeightSelected = true
        }

        list.filter { it.heartRate != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isHRSelected = true
            vitalsViewModel.isWeightSelected = false
        }

        list.filter { it.respRate != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isRRSelected = true
            vitalsViewModel.isWeightSelected = false
        }

        list.filter { it.spo2 != null }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isSpO2Selected = true
            vitalsViewModel.isWeightSelected = false
        }

        list.filter { it.bloodGlucose != null && it.bloodGlucoseType == BGEnum.RANDOM.value || it.bloodGlucoseType == BGEnum.FASTING.value }
            .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isGlucoseSelected = true
            vitalsViewModel.isWeightSelected = false
        }

        getBPListSize(list, vitalsViewModel) > 1 -> {
            vitalsViewModel.isBPSelected = true
            vitalsViewModel.isWeightSelected = false
        }
    }
}

@Preview
@Composable
private fun VitalScreenPreview() {
    VitalsScreen(rememberNavController())

}