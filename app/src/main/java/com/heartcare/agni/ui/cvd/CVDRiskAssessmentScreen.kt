package com.heartcare.agni.ui.cvd

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.AppointmentCompletedDialog
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.common.TabRowComposable
import com.heartcare.agni.ui.cvd.form.CVDRiskAssessmentForm
import com.heartcare.agni.ui.cvd.form.DisplayField
import com.heartcare.agni.ui.cvd.records.CVDRiskAssessmentRecords
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.HighRiskDarkContainer
import com.heartcare.agni.ui.theme.HighRiskLightContainer
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.LowRiskDarkContainer
import com.heartcare.agni.ui.theme.LowRiskLightContainer
import com.heartcare.agni.ui.theme.ModerateRiskCircle
import com.heartcare.agni.ui.theme.ModerateRiskDarkContainer
import com.heartcare.agni.ui.theme.ModerateRiskLightContainer
import com.heartcare.agni.ui.theme.VeryHighRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskDarkContainer
import com.heartcare.agni.ui.theme.VeryHighRiskLightContainer
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.REFERRAL_FROM_CVD
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMYYYYString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDRiskAssessmentScreen(
    navController: NavController,
    viewModel: CVDRiskAssessmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }
    val focusManager = LocalFocusManager.current
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSite by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<RecordType?>(null) }

    HandleLaunchedEffect(
        viewModel = viewModel,
        navController = navController,
        scope = scope,
        pagerState = pagerState,
        snackBarHostState = snackBarHostState,
        context = context,
        onCVDLoaded = { currentStep = 2 }
    )


    AddBackHandler(viewModel, navController, pagerState, scope)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.risk_predictor),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("TITLE")
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRowComposable(
                        viewModel.tabs,
                        pagerState
                    ) { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> {
                                Column(modifier = Modifier.fillMaxSize()) {

                                    when (currentStep) {
                                        0 -> {
                                            RecordTypeSelectionContent(
                                                modifier = Modifier.fillMaxSize(),
                                                selectedType = selectedType,
                                                onTypeSelected = { selectedType = it },
                                                onContinueClick = {
                                                    if (selectedType == RecordType.FACILITY) {
                                                        currentStep = 2
                                                    } else if (selectedType == RecordType.SCREENING_SITE) {
                                                        currentStep = 1
                                                    }
                                                }
                                            )
                                        }
                                        1 -> {
                                            ScreeningSiteListContent(
                                                modifier = Modifier.fillMaxSize(),
                                                sites =SITE_LIST,
                                                selectedSite = selectedSite,
                                                onSiteSelected = { selectedSite = it },
                                                onBackClick = {
                                                    currentStep = 0
                                                },
                                                onContinueClick = {
                                                    currentStep = 2
                                                }
                                            )
                                        }
                                        2 -> {
                                            CVDRiskAssessmentForm(viewModel)
                                        }
                                    }
                                }
                            }

                            1 -> CVDRiskAssessmentRecords(viewModel, navController)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentStep == 2)
            CVDBottomAppBar(viewModel, focusManager, scope, pagerState, snackBarHostState, context)
        }
    )
    if (viewModel.selectedRecord != null) {
        RecordsFullDetailsComposable(
            viewModel = viewModel,
            record = viewModel.selectedRecord!!,
            onClick = {
                viewModel.selectedRecord = null
            }
        )
    }
    Dialogs(
        viewModel = viewModel,
        navController = navController,
        scope = scope,
        pagerState = pagerState,
        snackBarHostState = snackBarHostState,
        context = context
    )
}

@Composable
private fun HandleLaunchedEffect(
    viewModel: CVDRiskAssessmentViewModel,
    navController: NavController,
    scope: CoroutineScope,
    pagerState: PagerState,
    snackBarHostState: SnackbarHostState,
    context: Context,
    onCVDLoaded: () -> Unit
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getAppointmentInfo(callback = {})
            viewModel.isLaunched = true
        }
        viewModel.getTodayCVDAssessment() {
            if (viewModel.todayCVD != null) {
                onCVDLoaded()
            }
        }
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            if (handle.remove<Boolean>(REFERRAL_FROM_CVD) == true){
                navigateToRecordsAfterSaving(
                    viewModel = viewModel,
                    scope = scope,
                    pagerState = pagerState,
                    snackBarHostState = snackBarHostState,
                    context = context
                )
            }
        }
    }
}

@Composable
private fun AddBackHandler(
    viewModel: CVDRiskAssessmentViewModel,
    navController: NavController,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    BackHandler {
        if (viewModel.selectedRecord != null) viewModel.selectedRecord = null
        else if (pagerState.currentPage == 1) {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else navController.navigateUp()
    }
}

@Composable
private fun CVDBottomAppBar(
    viewModel: CVDRiskAssessmentViewModel,
    focusManager: FocusManager,
    scope: CoroutineScope,
    pagerState: PagerState,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    if (pagerState.currentPage == 0) {
        Column {
            AnimatedVisibility(
                viewModel.riskPercentage.isNotBlank(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                RiskPercentageInfoComposable(viewModel)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.getRisk()
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = viewModel.ifFormValid() && viewModel.riskPercentage.isBlank()
                ) {
                    Text(text = stringResource(R.string.predict))
                }
                if (viewModel.riskPercentage.isNotBlank()) {
                    Button(
                        onClick = {
                            if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                                handleSaveButtonClick(
                                    viewModel,
                                    scope,
                                    snackBarHostState,
                                    context
                                )
                            } else {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        context.getString(R.string.patient_deceased_error_msg)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
private fun RiskPercentageInfoComposable(
    viewModel: CVDRiskAssessmentViewModel
) {
    if (viewModel.riskPercentage.isNotBlank()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = getContainerColor(viewModel.riskPercentage.toInt()),
            shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Canvas(modifier = Modifier.size(12.dp), onDraw = {
                            drawCircle(
                                color = getCircleColor(
                                    viewModel.riskPercentage.toIntOrNull() ?: 0
                                )
                            )
                        })
                        Text(
                            text = stringResource(
                                R.string.percentage,
                                viewModel.riskPercentage
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = stringResource(R.string.risk_info),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ActionMedicationInfo(
                    label = stringResource(R.string.action_colon),
                    info = viewModel.getRiskItem(viewModel.riskPercentage.toInt()).action
                )
                ActionMedicationInfo(
                    label = stringResource(R.string.medication_guidance_colon),
                    info = viewModel.getRiskItem(viewModel.riskPercentage.toInt()).medicationGuidance
                )
            }
        }
    }
}

private fun handleSaveButtonClick(
    viewModel: CVDRiskAssessmentViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    viewModel.checkIfCVDExistsForScreenDate(
        exists = {
            if (it) {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        context.getString(R.string.appointment_exists_on_screening_date)
                    )
                }
            } else {
                viewModel.getAppointmentInfo(
                    callback = {
                        when {
                            viewModel.existsInOtherHospital -> {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        context.getString(
                                            R.string.appointment_exists_in_other_hospital
                                        )
                                    )
                                }
                            }

                            viewModel.ifAllSlotsBooked -> viewModel.showAllSlotsBookedDialog = true

                            viewModel.canAddAssessment -> {
                                saveCVD(viewModel)
                            }

                            viewModel.isAppointmentCompleted -> viewModel.showAppointmentCompletedDialog = true

                            else -> viewModel.showAddToQueueDialog = true
                        }
                    }
                )
            }
        }
    )
}

private fun getCircleColor(riskPercentage: Int): Color {
    return when {
        riskPercentage < 10 -> LowRiskCircle
        riskPercentage in 10..19 -> ModerateRiskCircle
        riskPercentage in 20..29 -> HighRiskCircle
        else -> VeryHighRiskCircle
    }
}

@Composable
private fun getContainerColor(riskPercentage: Int): Color {
    return when (isSystemInDarkTheme()) {
        true -> when {
            riskPercentage < 10 -> LowRiskDarkContainer
            riskPercentage in 10..19 -> ModerateRiskDarkContainer
            riskPercentage in 20..29 -> HighRiskDarkContainer
            else -> VeryHighRiskDarkContainer
        }

        false -> when {
            riskPercentage < 10 -> LowRiskLightContainer
            riskPercentage in 10..19 -> ModerateRiskLightContainer
            riskPercentage in 20..29 -> HighRiskLightContainer
            else -> VeryHighRiskLightContainer
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordsFullDetailsComposable(
    viewModel: CVDRiskAssessmentViewModel,
    record: CVDResponse,
    onClick: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }

    ModalBottomSheet(
        onDismissRequest = {
            onClick()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = screenHeight * 0.8f)
                .navigationBarsPadding()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            verticalArrangement = Arrangement.Bottom
        ) {
            CVDRiskRow(record, onClick)
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DisplayField(
                    stringResource(R.string.diabetic_colon),
                    YesNoEnum.displayFromCode(record.diabetic)
                )
                DisplayField(
                    stringResource(R.string.current_smoker),
                    YesNoEnum.displayFromCode(record.smoker)
                )
                DisplayField(
                    stringResource(R.string.previous_heart_attack_or_stroke),
                    YesNoEnum.displayFromCode(record.heartAttackHistory)
                )
                DisplayField(
                    stringResource(R.string.blood_pressure_label),
                    "${record.bpSystolic}/${record.bpDiastolic} mmhg"
                )
                DisplayField(
                    stringResource(R.string.total_cholestrol),
                    if (record.cholesterol == null) stringResource(R.string.dash)
                    else "${record.cholesterol} ${record.cholesterolUnit}"
                )
                DisplayField(
                    stringResource(R.string.weight),
                    "${record.weight} ${record.weightUnit}"
                )
                DisplayField(
                    stringResource(R.string.height),
                    if (record.heightCm != null) "${record.heightCm} cm"
                    else if (record.heightFt != null || record.heightInch != null) "${record.heightFt} ft ${record.heightInch ?: 0} in"
                    else stringResource(R.string.dash)
                )

                ExtraInfoComposable(
                    label = stringResource(R.string.action),
                    info = viewModel.getRiskItem(record.risk).action,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )

                ExtraInfoComposable(
                    label = stringResource(R.string.medication_guidance),
                    info = viewModel.getRiskItem(record.risk).medicationGuidance,
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )

                if (!record.chiefComplaint.isNullOrBlank()) {
                    ExtraInfoComposable(
                        label = stringResource(R.string.chief_complaint),
                        info = record.chiefComplaint,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtraInfoComposable(
    label: String,
    info: String,
    containerColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = info,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CVDRiskRow(record: CVDResponse, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.percentage, record.risk.toString()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = record.createdOn.toddMMMyyyy(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = stringResource(
                    R.string.bmi,
                    record.bmi.toString()
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
        FilledTonalIconButton(
            onClick = {
                onClick()
            }
        ) {
            Icon(Icons.Default.Clear, Icons.Default.Clear.name)
        }
    }
}

@Composable
private fun ActionMedicationInfo(
    label: String,
    info: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = info,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Dialogs(
    viewModel: CVDRiskAssessmentViewModel,
    navController: NavController,
    scope: CoroutineScope,
    pagerState: PagerState,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    if (viewModel.showAddToQueueDialog) {
        AddToQueueComposable(viewModel)
    }
    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }
    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
    if (viewModel.showFollowUpDialog) {
        FollowUpDialog(
            viewModel = viewModel,
            navController = navController,
            scope = scope,
            pagerState = pagerState,
            snackBarHostState = snackBarHostState,
            context = context
        )
    }
}

@Composable
private fun AddToQueueComposable(
    viewModel: CVDRiskAssessmentViewModel
) {
    CustomDialog(
        title = if (viewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
            id = R.string.add_to_queue_question
        ),
        text = stringResource(id = R.string.add_to_queue_assessment_dialog_description),
        dismissBtnText = stringResource(id = R.string.dismiss),
        confirmBtnText = if (viewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
            id = R.string.add_to_queue
        ),
        dismiss = { viewModel.showAddToQueueDialog = false },
        confirm = {
            if (viewModel.appointment != null) {
                viewModel.updateStatusToArrived(
                    viewModel.patient!!,
                    viewModel.appointment!!,
                    updated = {
                        viewModel.showAddToQueueDialog = false
                        saveCVD(viewModel)
                    }
                )
            } else {
                if (viewModel.ifAllSlotsBooked) {
                    viewModel.showAllSlotsBookedDialog = true
                } else {
                    viewModel.addPatientToQueue(
                        viewModel.patient!!,
                        addedToQueue = {
                            viewModel.showAddToQueueDialog = false
                            saveCVD(viewModel)
                        }
                    )
                }
            }
        }
    )
}

private fun saveCVD(
    viewModel: CVDRiskAssessmentViewModel
) {
    viewModel.saveCVDRecord(
        saved = {
            viewModel.showFollowUpDialog = true
        }
    )
}

@Composable
private fun FollowUpDialog(
    viewModel: CVDRiskAssessmentViewModel,
    navController: NavController,
    scope: CoroutineScope,
    pagerState: PagerState,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    val referralRequired = viewModel.getRiskItem(viewModel.riskPercentage.toInt()).requiresReferral
            && !viewModel.isReferralAlreadyExists

    val title = if (referralRequired)
        stringResource(R.string.follow_up_and_refer_dialog_title)
    else null
    val text = if (referralRequired)
        stringResource(R.string.follow_up_and_refer_dialog_text, viewModel.followUpDate!!.toddMMYYYYString())
    else stringResource(R.string.follow_up_dialog_text, viewModel.followUpDate!!.toddMMYYYYString())
    CustomDialog(
        canBeDismissed = false,
        title = title,
        text = text,
        dismissBtnText = if (referralRequired) stringResource(R.string.dismiss) else null,
        confirmBtnText = if (referralRequired) stringResource(R.string.refer_patient) else stringResource(R.string.dismiss),
        dismiss = {
            navigateToRecordsAfterSaving(
                viewModel,
                scope,
                pagerState,
                snackBarHostState,
                context
            )
        },
        confirm = {
            if (referralRequired) {
                viewModel.showFollowUpDialog = false
                scope.launch {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        PATIENT,
                        viewModel.patient!!
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        REFERRAL_FROM_CVD,
                        true
                    )
                    navController.navigate(Screen.AddReferralScreen.route)
                }
            } else {
                navigateToRecordsAfterSaving(
                    viewModel,
                    scope,
                    pagerState,
                    snackBarHostState,
                    context
                )
            }
        }
    )
}

private fun navigateToRecordsAfterSaving(
    viewModel: CVDRiskAssessmentViewModel,
    scope: CoroutineScope,
    pagerState: PagerState,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    viewModel.showFollowUpDialog = false
    viewModel.clearForm()
    viewModel.getTodayCVDAssessment()
    scope.launch {
        pagerState.animateScrollToPage(1)
        snackBarHostState.showSnackbar(
            message = context.getString(R.string.assessment_record_saved)
        )
    }
}