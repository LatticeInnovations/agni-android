package com.heartcare.agni.ui.vitalsscreen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.common.displayOrDash
import com.heartcare.agni.ui.cvd.form.DisplayField
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.VitalLabel
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.ui.vitalsscreen.components.CustomChip
import com.heartcare.agni.ui.vitalsscreen.components.LineChartView
import com.heartcare.agni.ui.vitalsscreen.components.LineChartViewGlucose
import com.heartcare.agni.ui.vitalsscreen.components.SegmentedButtonForVital
import com.heartcare.agni.ui.vitalsscreen.enums.BGEnum
import com.heartcare.agni.ui.vitalsscreen.enums.VitalsTrendEnum
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.VitalConstants.ALL
import com.heartcare.agni.utils.constants.VitalConstants.CVD_RECORD
import com.heartcare.agni.utils.constants.VitalConstants.LIST_TYPE_CVD
import com.heartcare.agni.utils.constants.VitalConstants.LIST_TYPE_VITAL
import com.heartcare.agni.utils.constants.VitalConstants.VITAL_UPDATE_OR_ADD
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.convertDateFormat
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.convertedDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.formatDateToDayMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(navController: NavController, vitalsViewModel: VitalsViewModel = hiltViewModel()) {

    HandleLaunchedEffect(vitalsViewModel, navController)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSite by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<RecordType?>(null) }

    BackHandler {
        if (currentStep > 0) {
            if (currentStep == 2) {
                currentStep = 1
            } else if (currentStep == 1) {
                currentStep = 0
            }
        } else {
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = vitalsViewModel.msg) {
        if (vitalsViewModel.msg.isNotBlank()) {
            vitalsViewModel.getVitalsAndCVDRecords()
            snackBarHostState.showSnackbar(vitalsViewModel.msg)
            vitalsViewModel.msg = ""
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
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
                }
            )
        },
        content = { paddingValues ->
            when (currentStep) {
                0 -> ShowGraphAndList(vitalsViewModel, paddingValues)
                1 -> RecordTypeSelectionContent(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    onContinueClick = {
                        if (selectedType == RecordType.FACILITY) {
                            handleAddVitalLogic(vitalsViewModel, navController, scope, snackBarHostState, context)
                        } else if (selectedType == RecordType.SCREENING_SITE) {
                            currentStep = 2
                        }
                    }
                )
                2 -> ScreeningSiteListContent(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    sites = listOf(
                        "Shefa Vaccine Drive",
                        "Tanna Island Outreach Q2",
                        "Torba Nutrition Program",
                        "Santo Malaria Prevention"
                    ),
                    selectedSite = selectedSite,
                    onSiteSelected = { selectedSite = it },
                    onBackClick = { currentStep = 1 },
                    onContinueClick = {
                        handleAddVitalLogic(vitalsViewModel, navController, scope, snackBarHostState, context)
                    }
                )
            }
        },
        bottomBar = {
            if (currentStep == 0) {
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
                                .fillMaxWidth(),
                            onClick = {
                                if (vitalsViewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                                    if (vitalsViewModel.todayVital != null && !vitalsViewModel.existsInOtherHospital) {
                                        handleAddVitalLogic(vitalsViewModel, navController, scope, snackBarHostState, context)
                                    } else {
                                        currentStep = 1
                                    }
                                } else {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(
                                            context.getString(R.string.patient_deceased_error_msg)
                                        )
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
                                if (vitalsViewModel.todayVital == null || vitalsViewModel.existsInOtherHospital)
                                    stringResource(id = R.string.add_vitals)
                                else stringResource(id = R.string.update_vitals)
                            )
                        }
                    }
                }
            }
        }
    )
    ShowDialogs(vitalsViewModel, navController, scope)
}

private fun handleAddVitalLogic(
    viewModel: VitalsViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    viewModel.getAppointmentInfo(
        callback = {
            when {
                viewModel.existsInOtherHospital -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.appointment_exists_in_other_hospital)
                        )
                    }
                }

                viewModel.canAddAssessment -> {
                    coroutineScope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient
                        )
                        navController.navigate(Screen.AddVitalsScreen.route)
                    }
                }

                viewModel.isAppointmentCompleted -> {
                    viewModel.showAppointmentCompletedDialog = true
                }

                else -> {
                    viewModel.showAddToQueueDialog = true
                }
            }
        }
    )
}

@Composable
private fun ShowGraphAndList(
    vitalsViewModel: VitalsViewModel,
    paddingValues: PaddingValues
) {
    val vitals by vitalsViewModel._vitals.collectAsState()
    val combinedList =
        remember(vitals, vitalsViewModel.previousRecords, vitalsViewModel.selectedOption) {
            getFilteredCombinedList(vitalsViewModel, vitals)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (vitalsViewModel.isVitalExist || vitalsViewModel.previousRecords.isNotEmpty()) {
            VitalsContent(
                vitalsViewModel = vitalsViewModel,
                combinedList = combinedList
            )
        } else {
            NoRecordFoundMessage()
        }
    }
}

@Composable
private fun VitalsContent(
    vitalsViewModel: VitalsViewModel,
    combinedList: List<CombineVitalAndCVDRecord>
) {
    LazyColumn {
        item { VitalsTrendGraph(vitalsViewModel) }

        item {
            SegmentedButtonForVital(
                options = listOf(ALL, CVD_RECORD),
                isCVDListEmpty = vitalsViewModel.previousRecords.isEmpty(),
                selectedOption = vitalsViewModel.selectedOption,
                onOptionSelected = { vitalsViewModel.selectedOption = it }
            )
        }

        items(items = combinedList) { item ->
            when (item.type) {
                LIST_TYPE_VITAL -> VitalsCardLayout(vital = item.content as VitalResponse)
                LIST_TYPE_CVD -> CVDRecordCardLayout(cvdResponse = item.content as CVDResponse)
            }
        }
    }
}

@Composable
private fun NoRecordFoundMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Black else White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_record_found),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

private fun getFilteredCombinedList(
    viewModel: VitalsViewModel,
    vitals: List<VitalResponse>
): List<CombineVitalAndCVDRecord> {
    val combinedList = getCombinedList(viewModel, vitals)
    return if (viewModel.selectedOption == CVD_RECORD) {
        combinedList.filter { it.type == LIST_TYPE_CVD }
    } else combinedList
}

private fun getCombinedList(
    vitalsViewModel: VitalsViewModel, vitals: List<VitalResponse>
): List<CombineVitalAndCVDRecord> {
    return (vitals.map { vital ->
        CombineVitalAndCVDRecord(
            LIST_TYPE_VITAL, vital.appUpdatedDate, vital
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
            if (!isLaunched) {
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                patient?.let {
                    getAppointmentInfo {}
                    getVitalsAndCVDRecords()
                }
            }
            msg = navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                VITAL_UPDATE_OR_ADD
            ) ?: ""
            isLaunched = true
        }
    }
}

@Composable
private fun ShowDialogs(
    vitalsViewModel: VitalsViewModel, navController: NavController, scope: CoroutineScope
) {
    if (vitalsViewModel.showAddToQueueDialog) {
        CustomDialog(
            title = if (vitalsViewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
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
private fun VitalsTrendGraph(
    vitalsViewModel: VitalsViewModel,
    modifier: Modifier = Modifier
) {
    val list by vitalsViewModel._vitals.collectAsState()

    if (
        list.filter { it.bloodGlucose != null }.groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1
        || vitalsViewModel.previousRecords.groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1
    ) {
        ShowTrendGraphCard(modifier, vitalsViewModel, list.reversed())
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = if (isSystemInDarkTheme()) Black else White
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
private fun ShowTrendGraphCard(
    modifier: Modifier,
    vitalsViewModel: VitalsViewModel,
    list: List<VitalResponse>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = if (isSystemInDarkTheme()) Black else White
            )
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.vitals_trend),
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
            )
        )
        LaunchedEffect(key1 = vitalsViewModel.isFirstLaunch) {
            if (!vitalsViewModel.isFirstLaunch) {
                showChipSelection(vitalsViewModel, list)
                vitalsViewModel.isFirstLaunch = true
            }
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = vitalsViewModel.previousRecords
                    .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1
            ) {
                CustomChip(
                    idSelected = vitalsViewModel.isWeightSelected,
                    label = VitalsTrendEnum.Weight.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = true
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }
            }
            AnimatedVisibility(visible = list.filter { it.bloodGlucose != null }
                .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1) {

                CustomChip(
                    idSelected = vitalsViewModel.isGlucoseSelected,
                    label = VitalsTrendEnum.Glucose.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isGlucoseSelected = true
                        isBPSelected = false
                    }
                }
            }
            AnimatedVisibility(
                visible = vitalsViewModel.previousRecords
                    .groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1
            ) {
                CustomChip(
                    idSelected = vitalsViewModel.isBPSelected, label = VitalsTrendEnum.BP.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isGlucoseSelected = false
                        isBPSelected = true
                    }
                }
            }
        }
        AnimatedVisibility(vitalsViewModel.isGlucoseSelected) {
            GraphLegendGroup(
                legendList = listOf(
                    Pair(BGEnum.RANDOM.value.capitalizeFirst(), VitalLabel),
                    Pair(
                        BGEnum.FASTING.value.capitalizeFirst(),
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

        if (!vitalsViewModel.isGlucoseSelected && (vitalsViewModel.isWeightSelected || vitalsViewModel.isBPSelected)) {
            LineChartView(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .height(200.dp),
                entries1 = getChartEntries(list, vitalsViewModel),
                entries2 = getChartEntries2(list, vitalsViewModel),
                labels = getLabels(vitalsViewModel, list),
                isBp = vitalsViewModel.isBPSelected
            )
            Timber.d("List : ${list.map { it.appUpdatedDate.formatDateToDayMonth() }}")
        } else if (vitalsViewModel.isGlucoseSelected) {
            LineChartViewGlucose(
                modifier = modifier
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

private fun getLabels(vitalsViewModel: VitalsViewModel, list: List<VitalResponse>): List<String> {
    return if (vitalsViewModel.isGlucoseSelected) {
        // labels from vitals
        (list.map { it.appUpdatedDate }).distinct().sorted().map { it.formatDateToDayMonth() }
            .distinct()
    } else {
        // labels from CVD
        vitalsViewModel.previousRecords.map { it.createdOn }.distinct().sorted().map { it.formatDateToDayMonth() }.distinct()
    }
}

@Composable
private fun GraphLegendGroup(legendList: List<Pair<String, Color>>) {
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
private fun IndicatorWithText(color: Color, text: String) {
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

private fun getChartEntries(
    list: List<VitalResponse>,
    vitalsViewModel: VitalsViewModel
): List<Entry>? {

    return when {
        vitalsViewModel.isWeightSelected -> {
            getEntriesFromCVD(vitalsViewModel.previousRecords) {
                if (it.weightUnit == vitalsViewModel.kg) it.weight.toFloat()
                else it.weight.toFloat().times(0.45359236f)
            }
        }

        vitalsViewModel.isGlucoseSelected -> {
            getEntries(
                list.filter { it.bloodGlucose?.type == BGEnum.FASTING.value },
                list
            ) {
                if (it.bloodGlucose?.unit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose.value.toFloat()
                    .times(18.018f)
                else it.bloodGlucose?.value?.toFloat()
            }
        }

        vitalsViewModel.isBPSelected -> {
            getEntriesFromCVD(vitalsViewModel.previousRecords) { it.bpSystolic.toFloat() }
        }

        else -> {
            null
        }
    }

}

private fun getChartEntries2(
    list: List<VitalResponse>,
    vitalsViewModel: VitalsViewModel
): List<Entry>? {
    return if (vitalsViewModel.isGlucoseSelected) {
        getEntries(
            list.filter { it.bloodGlucose?.type == BGEnum.RANDOM.value }, list
        ) {
            if (it.bloodGlucose?.unit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose.value.toFloat()
                .times(18.018f).roundToInt().toFloat()
            else it.bloodGlucose?.value?.toFloat()
        }
    } else if (vitalsViewModel.isBPSelected) {
        getEntriesFromCVD(
            vitalsViewModel.previousRecords
        ) { it.bpDiastolic.toFloat() }
    } else {
        null
    }

}

private fun getEntries(
    list: List<VitalResponse>,
    bgList: List<VitalResponse>? = null,
    valueSelector: (VitalResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group the list by formatted dates
    val filteredList = list.groupBy { it.appUpdatedDate.formatDateToDayMonth() }
    val vitalGroupedByDate = bgList?.groupBy { it.appUpdatedDate.formatDateToDayMonth() }
        ?: list.groupBy { it.appUpdatedDate.formatDateToDayMonth() }

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

private fun getEntriesFromCVD(
    list: List<CVDResponse>,
    valueSelector: (CVDResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group the list by formatted dates
    val filteredList = list.groupBy { it.createdOn.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    (filteredList.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }.also { allDates ->
            Timber.d("Date: $allDates")
            for (date in allDates) {

                // Find the index of the date in the labels list
                val labelIndex = allDates.indexOf(date)
                if (labelIndex != -1) {
                    Timber.d(
                        "Value: $labelIndex: $date :\n${
                            filteredList[date]?.mapNotNull(
                                valueSelector
                            ).orEmpty()
                        }"
                    )
                    // Calculate the average value for the grouped records

                    val values: List<Float> = filteredList[date]?.mapNotNull(valueSelector)
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

@Composable
private fun VitalsCardLayout(
    modifier: Modifier = Modifier,
    vital: VitalResponse
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = vital.appUpdatedDate.convertedDate().convertDateFormat(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = vital.practitionerName ?: stringResource(id = R.string.dash),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DisplayVitalFieldColumn(
                    label = "${
                        stringResource(
                            id = if (vital.bloodGlucose?.type == stringResource(
                                    id = R.string.fasting
                                ).lowercase()
                            ) R.string.fasting else R.string.random
                        )
                    } ${
                        stringResource(
                            id = R.string.blood_glucose
                        ).lowercase()
                    }",
                    value = vital.bloodGlucose?.let { "${it.value} ${it.unit}" }
                )
                DisplayVitalFieldColumn(
                    label = stringResource(R.string.foot_examination),
                    value = vital.footExamination
                )
                DisplayVitalFieldColumn(
                    label = stringResource(R.string.eye_examination),
                    value = vital.eyeExamination
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.abdominal_circumference),
                    value = vital.abdominalCircumference?.let { "${it.value} ${it.unit}" }
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.hip_circumference),
                    value = vital.hipCircumference?.let { "${it.value} ${it.unit}" }
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.HbA1c),
                    value = vital.hbA1cPercentage?.run { "$this%" }
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.serum_creatinine),
                    value = vital.serumCreatinine?.let { "${it.value} ${it.unit}" }
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.serum_potassium),
                    value = vital.serumPotassium?.let { "${it.value} ${it.unit}" }
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.urine_protein),
                    value = vital.urineProtein
                )
                DisplayVitalFieldRow(
                    label = stringResource(R.string.urine_ketones),
                    value = vital.urineKetones
                )
                DisplayVitalFieldColumn(
                    label = stringResource(R.string.other),
                    value = vital.others
                )
            }
        }
    }
}

@Composable
private fun DisplayVitalFieldColumn(
    label: String,
    value: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value.displayOrDash(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DisplayVitalFieldRow(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value.displayOrDash(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CVDRecordCardLayout(
    modifier: Modifier = Modifier,
    cvdResponse: CVDResponse
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = cvdResponse.createdOn.convertedDate().convertDateFormat(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer

                    )
                    Text(
                        text = cvdResponse.practitionerName ?: stringResource(id = R.string.dash),
                        style = MaterialTheme.typography.bodySmall
                    )

                }
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    color = Color.Transparent
                ) {
                    Text(
                        text = stringResource(R.string.cvd_percentage, cvdResponse.risk),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DisplayField(
                    label = stringResource(R.string.bmi_label),
                    value = cvdResponse.bmi.toString()
                )
                DisplayField(
                    stringResource(R.string.diabetic_colon),
                    YesNoEnum.displayFromCode(cvdResponse.diabetic)
                )
                DisplayField(
                    stringResource(R.string.current_smoker),
                    YesNoEnum.displayFromCode(cvdResponse.smoker)
                )
                DisplayField(
                    stringResource(R.string.previous_heart_attack_or_stroke),
                    YesNoEnum.displayFromCode(cvdResponse.heartAttackHistory)
                )
                DisplayField(
                    label = stringResource(id = R.string.blood_pressure),
                    value = if (cvdResponse.bpDiastolic != 0 && cvdResponse.bpSystolic != 0) "${cvdResponse.bpSystolic}/${cvdResponse.bpDiastolic} ${
                        stringResource(
                            id = R.string.mmhg
                        )
                    }" else stringResource(id = R.string.dashes)
                )

                DisplayField(
                    label = stringResource(id = R.string.total_cholestrol),
                    value = if (cvdResponse.cholesterol != null) "${cvdResponse.cholesterol} ${cvdResponse.cholesterolUnit}" else stringResource(
                        id = R.string.dashes
                    )
                )
                DisplayField(
                    stringResource(R.string.weight),
                    "${cvdResponse.weight} ${cvdResponse.weightUnit}"
                )
                DisplayField(
                    label = stringResource(id = R.string.height),
                    value = setCVDHeight(cvdResponse)
                )
                if (!cvdResponse.chiefComplaint.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.chief_complaint),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = cvdResponse.chiefComplaint,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun setCVDHeight(cvdResponse: CVDResponse): String {
    return when {
        cvdResponse.heightCm != null -> {
            "${cvdResponse.heightCm} ${stringResource(id = R.string.cm)}"
        }

        cvdResponse.heightFt != null || cvdResponse.heightInch != null -> {
            "${cvdResponse.heightFt ?: ""} ft ${cvdResponse.heightInch ?: "0"} in"
        }

        else -> {
            stringResource(
                id = R.string.dashes
            )
        }
    }
}

private fun showChipSelection(vitalsViewModel: VitalsViewModel, list: List<VitalResponse>) {
    when {
        vitalsViewModel.previousRecords.groupBy { it.createdOn.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isWeightSelected = true
        }

        list.filter { it.bloodGlucose != null }
            .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isGlucoseSelected = true
            vitalsViewModel.isWeightSelected = false
        }
    }
}

@Preview
@Composable
private fun VitalScreenPreview() {
    VitalsScreen(rememberNavController())
}